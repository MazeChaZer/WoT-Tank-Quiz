/*  Copyright 2012 Jonas Schürmann ©

    My Website: mazechazer.jimdo.com

    This file is part of  the WoT Tank Quiz.

    The WoT Tank Quiz is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The WoT Tank Quiz is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the WoT Tank Quiz.  If not, see <http://www.gnu.org/licenses/>. */

package mazechazer.android.wottankquiz;

public class Tank {
	String name;
	Country country;
	TankClass tankClass;
	int resID;
	Tank() {
	}
	Tank(String nameArg, int resIDArg, Country countryArg, TankClass tankClassArg){
		name = nameArg;
		resID = resIDArg;
		country = countryArg;
		tankClass = tankClassArg;
	}
}
