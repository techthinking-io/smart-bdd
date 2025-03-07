/*
 * Flow BDD - The productive way to test.
 * Copyright (C)  2021  James Bayliss
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.cucumbers.builders;

import com.example.cucumbers.model.CucumberWhen;
import io.techthinking.flowbdd.bdd.report.utils.WhenBuilder;

public class CucumberWhenBuilder implements WhenBuilder<CucumberWhen> {
    private String colour;

    private CucumberWhenBuilder() {
    }

    public static CucumberWhenBuilder iRequestToEatACucumber() {
        return new CucumberWhenBuilder();
    }

    public CucumberWhenBuilder withColour(String colour) {
        this.colour = colour;
        return this;
    }

    public CucumberWhen build() {
        return new CucumberWhen(colour);
    }
}
