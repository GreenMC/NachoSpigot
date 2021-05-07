package io.github.greenmc.greenspigot.console.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Despical
 * <p>
 * Created at 6.05.2021
 */
@Plugin(name = "TerminalConsole", category = "Core", elementType = "appender", printObject = true)
public class TerminalConsole extends AbstractAppender {

    private static boolean initialized;
    private static Terminal terminal;
    private static LineReader reader;

    protected TerminalConsole(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        initializeTerminal();
    }

    private static synchronized void initializeTerminal() {
        if (!initialized) {
            initialized = true;
            Boolean jlineOverride = getOptionalBooleanProperty("terminal.jline");
            boolean dumb = jlineOverride == Boolean.TRUE || System.getProperty("java.class.path").contains("idea_rt.jar");

            if (jlineOverride != Boolean.FALSE) {
                try {
                    terminal = TerminalBuilder.builder().dumb(dumb).build();
                } catch (IllegalStateException var3) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.warn("Disabling terminal, you're running in an unsupported environment.", var3);
                    } else {
                        LOGGER.warn("Disabling terminal, you're running in an unsupported environment.");
                    }
                } catch (IOException var4) {
                    LOGGER.error("Failed to initialize terminal. Falling back to standard output", var4);
                }
            }
        }
    }

    public void append(LogEvent event) {
        print(getLayout().toSerializable(event).toString());
    }

    private static synchronized void print(String text) {
        if (terminal != null) {
            if (reader != null) {
                reader.printAbove(text);
            } else {
                terminal.writer().print(text);
                terminal.writer().flush();
            }
        } else {
            System.out.print(text);
        }
    }

    public static synchronized void setReader(LineReader newReader) {
        if (newReader != null && newReader.getTerminal() != terminal) {
            throw new IllegalArgumentException("Reader was not created with TerminalConsole.getTerminal()");
        } else {
            reader = newReader;
        }
    }

    public static synchronized void close() throws IOException {
        if (initialized) {
            initialized = false;
            reader = null;

            if (terminal != null) {
                try {
                    terminal.close();
                } finally {
                    terminal = null;
                }
            }
        }
    }

    public static boolean isAnsiSupported() {
        Boolean ANSI_OVERRIDE = getOptionalBooleanProperty("terminal.ansi");
        return ANSI_OVERRIDE != null ? ANSI_OVERRIDE : terminal != null;
    }

    @PluginFactory
    public static TerminalConsole createAppender(@PluginAttribute("name") String name,
                                                 @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                 @PluginElement("Filter") final Filter filter) {

        return new TerminalConsole(name, filter, layout, true);
    }

    private static Boolean getOptionalBooleanProperty(String name) {
        String value = PropertiesUtil.getProperties().getStringProperty(name);

        if (value == null) {
            return null;
        } else if (value.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (value.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        } else {
            LOGGER.warn("Invalid value for boolean input property '{}': {}", name, value);
            return null;
        }
    }
}