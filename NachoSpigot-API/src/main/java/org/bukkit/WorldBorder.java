package org.bukkit;

public interface WorldBorder {

    /**
     * Resets the border to default values.
     */
     void reset();

    /**
     * Gets the current side length of the border.
     *
     * @return The current side length of the border.
     */
     double getSize();

    /**
     * Sets the border to a square region with the specified side length in blocks.
     *
     * @param newSize The new size of the border.
     */
     void setSize(double newSize);

    /**
     * Sets the border to a square region with the specified side length in blocks.
     *
     * @param newSize The new side length of the border.
     * @param seconds The time in seconds in which the border grows or shrinks from the previous size to that being set.
     */
     void setSize(double newSize, long seconds);

    /**
     * Gets the current border center.
     *
     * @return The current border center.
     */
     Location getCenter();

    /**
     * Sets the new border center.
     *
     * @param x The new center x-coordinate.
     * @param z The new center z-coordinate.
     */
     void setCenter(double x, double z);

    /**
     * Sets the new border center.
     *
     * @param location The new location of the border center. (Only x/z used)
     */
     void setCenter(Location location);

    /**
     * Gets the current border damage buffer.
     *
     * @return The current border damage buffer.
     */
     double getDamageBuffer();

    /**
     * Sets the amount of blocks a player may safely be outside the border before taking damage.
     *
     * @param blocks The amount of blocks. (The default is 5 blocks.)
     */
     void setDamageBuffer(double blocks);

    /**
     * Gets the current border damage amount.
     *
     * @return The current border damage amount.
     */
     double getDamageAmount();

    /**
     * Sets the amount of damage a player takes when outside the border plus the border buffer.
     *
     * @param damage The amount of damage. (The default is 0.2 damage per second per block.)
     */
     void setDamageAmount(double damage);

    /**
     * Gets the current border warning time in seconds.
     *
     * @return The current border warning time in seconds.
     */
     int getWarningTime();

    /**
     * Sets the warning time that causes the screen to be tinted red when a contracting border will reach the player within the specified time.
     *
     * @param seconds The amount of time in seconds. (The default is 15 seconds.)
     */
     void setWarningTime(int seconds);

    /**
     * Gets the current border warning distance.
     *
     * @return The current border warning distance.
     */
     int getWarningDistance();

    /**
     * Sets the warning distance that causes the screen to be tinted red when the player is within the specified number of blocks from the border.
     *
     * @param distance The distance in blocks. (The default is 5 blocks.)
     */
     void setWarningDistance(int distance);
}
