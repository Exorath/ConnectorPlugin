/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.plugin.connector.world;


import com.exorath.commons.ItemStackSerialize;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

/**
 * Created by toonsev on 5/27/2017.
 */
public class ArmorStandConfiguration {
    private double x;
    private double y;
    private double z;

    private Angle headPose;
    private Angle bodyPose;
    private Angle leftArmPose;
    private Angle rightArmPose;
    private Angle leftLegPose;
    private Angle rightLegPose;

    private boolean arms = false;
    private boolean basePlate = false;
    private boolean small = false;


    private ArmorStandArmor armor;

    public ArmorStandConfiguration() {
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public ArmorStandArmor getArmor() {
        return armor;
    }

    public Angle getHeadPose() {
        return headPose;
    }

    public Angle getBodyPose() {
        return bodyPose;
    }

    public Angle getLeftArmPose() {
        return leftArmPose;
    }

    public Angle getRightArmPose() {
        return rightArmPose;
    }

    public Angle getLeftLegPose() {
        return leftLegPose;
    }

    public Angle getRightLegPose() {
        return rightLegPose;
    }

    public boolean isArms() {
        return arms;
    }

    public boolean isBasePlate() {
        return basePlate;
    }

    public boolean isSmall() {
        return small;
    }

    public ArmorStand load(World world) {
        ArmorStand armorStand = world.spawn(new Location(world, x, y, z), ArmorStand.class);
        armorStand.setArms(arms);
        armorStand.setBasePlate(basePlate);
        armorStand.setHeadPose(headPose);
        armorStand.setBodyPose(bodyPose);
        armorStand.setLeftArmPose(leftArmPose);
        armorStand.setRightArmPose(rightArmPose);
        armorStand.setLeftLegPose(leftLegPose);
        armorStand.setRightLegPose(rightLegPose);
        armorStand.setSmall(small);
        if (armor != null) {
            armorStand.setHelmet(ItemStackSerialize.toItemStack(armor.getHelmet()));
            armorStand.setChestplate(ItemStackSerialize.toItemStack(armor.getChestPlate()));
            armorStand.setLeggings(ItemStackSerialize.toItemStack(armor.getLeggings()));
            armorStand.setBoots(ItemStackSerialize.toItemStack(armor.getBoots()));
            armorStand.setItemInHand(ItemStackSerialize.toItemStack(armor.getHand()));
        }
        return armorStand;
    }
}
