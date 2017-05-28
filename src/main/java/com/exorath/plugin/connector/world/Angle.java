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

import org.bukkit.util.EulerAngle;

/**
 * ANGLES ARE IN DEGREES, EULERANGLES IN RADIANS!
 * Created by toonsev on 5/27/2017.
 */
public class Angle {
    private double x;
    private double y;
    private double z;

    public Angle(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Angle() {
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

    public EulerAngle toEuler(){
        return new EulerAngle(degreesToRadians(x),degreesToRadians(y), degreesToRadians(z));
    }


    public static double degreesToRadians(double degrees){

        return (degrees * Math.PI)/180d;

    }

}
