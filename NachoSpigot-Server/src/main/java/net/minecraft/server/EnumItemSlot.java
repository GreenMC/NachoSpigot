package net.minecraft.server;

public enum EnumItemSlot {
    MAINHAND(Function.HAND, 0, 0, "mainhand"),
    FEET(Function.ARMOR, 0, 1, "feet"),
    LEGS(Function.ARMOR, 1, 2, "legs"),
    CHEST(Function.ARMOR, 2, 3, "chest"),
    HEAD(Function.ARMOR, 3, 4, "head");

    private final Function g;
    private final int h;
    private final int i;
    private final String j;

    EnumItemSlot(EnumItemSlot.Function enumitemslot_function, int i, int j, String s) {
        this.g = enumitemslot_function;
        this.h = i;
        this.i = j;
        this.j = s;
    }

    public Function getType() {
        return this.a();
    }

    public Function a() {
        return this.g;
    }

    public int b() {
        return this.h;
    }

    public int getSlotFlag() {
        return this.i;
    }

    public String getSlotName() {
        return this.j;
    }

    public static EnumItemSlot fromName(String s) {
        for (EnumItemSlot enumitemslot : EnumItemSlot.values()) {
            if (!enumitemslot.getSlotName().equals(s)) continue;
            return enumitemslot;
        }
        throw new IllegalArgumentException("Invalid slot '" + s + "'");
    }

    public static EnumItemSlot a(Function enumitemslot_function, int i) {
        for (EnumItemSlot enumitemslot : EnumItemSlot.values()) {
            if (enumitemslot.a() != enumitemslot_function || enumitemslot.b() != i) continue;
            return enumitemslot;
        }
        throw new IllegalArgumentException("Invalid slot '" + enumitemslot_function + "': " + i);
    }

    public enum Function {
        HAND, ARMOR
    }
}
