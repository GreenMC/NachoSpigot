package org.bukkit.entity;

import org.bukkit.DyeColor;
import org.bukkit.material.Colorable;

/**
 * Represents a Sheep.
 */
public interface Sheep extends Animals, Colorable {

    /**
     * @return Whether the sheep is sheared.
     */
    boolean isSheared();

    /**
     * @param flag Whether to shear the sheep
     */
    void setSheared(boolean flag);

    DyeColor getColor();

    void setColor(DyeColor color);


}
