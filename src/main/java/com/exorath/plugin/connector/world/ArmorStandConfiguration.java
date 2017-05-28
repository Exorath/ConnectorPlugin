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
import com.exorath.plugin.connector.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by toonsev on 5/27/2017.
 */
public class ArmorStandConfiguration {
    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    private Angle headPose;
    private Angle bodyPose;
    private Angle leftArmPose;
    private Angle rightArmPose;
    private Angle leftLegPose;
    private Angle rightLegPose;


    private transient boolean loaded = false;
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
        if(world.getChunkAt((int) x, (int) z).isLoaded()) {
            System.out.println("Spawning armorstand at [" + x + "," + y + "," + z + "(" + yaw + "," + pitch + ")]");
            ArmorStand armorStand = world.spawn(new Location(world, x, y, z, yaw, pitch), ArmorStand.class,
                    armorStand1 -> {
                        armorStand1.setMetadata("connector", new FixedMetadataValue(Main.getInstance(), ""));
                        armorStand1.setMetadata("doNotDespawn", new FixedMetadataValue(Main.getInstance(), ""));

                        armorStand1.setArms(arms);
                        armorStand1.setBasePlate(basePlate);
                        if (headPose != null)
                            armorStand1.setHeadPose(headPose.toEuler());
                        if (bodyPose != null)
                            armorStand1.setBodyPose(bodyPose.toEuler());
                        if (leftArmPose != null)
                            armorStand1.setLeftArmPose(leftArmPose.toEuler());
                        if (rightArmPose != null)
                            armorStand1.setRightArmPose(rightArmPose.toEuler());
                        if (leftLegPose != null)
                            armorStand1.setLeftLegPose(leftLegPose.toEuler());
                        if (rightLegPose != null)
                            armorStand1.setRightLegPose(rightLegPose.toEuler());
                        armorStand1.setSmall(small);
                        if (armor != null) {
                            if (armor.getHelmet() != null)
                                armorStand1.setHelmet(ItemStackSerialize.toItemStack(armor.getHelmet()));
                            if (armor.getChestPlate() != null)
                                armorStand1.setChestplate(ItemStackSerialize.toItemStack(armor.getChestPlate()));
                            if (armor.getLeggings() != null)
                                armorStand1.setLeggings(ItemStackSerialize.toItemStack(armor.getLeggings()));
                            if (armor.getBoots() != null)
                                armorStand1.setBoots(ItemStackSerialize.toItemStack(armor.getBoots()));
                            if (armor.getHand() != null)
                                armorStand1.setItemInHand(ItemStackSerialize.toItemStack(armor.getHand()));
                            if(armor.getOffHand() != null)
                                armorStand1.getEquipment().setItemInOffHand(ItemStackSerialize.toItemStack(armor.getOffHand()));
                        }
                    });

            loaded = true;
            return armorStand;
        }
        return null;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
