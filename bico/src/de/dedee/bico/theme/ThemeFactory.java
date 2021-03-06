/*  
 * bico - (C)opyright 2012 - dedee
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dedee.bico.theme;

import android.content.Context;
import de.dedee.bico.ui.Resolution;

public class ThemeFactory {

	public static Theme createTheme(Context context, Resolution resolution, String themeId) {

		if (themeId == "BiCo text small")
			return new DefaultTheme32(context, resolution);
		else if (themeId == "BiCo text large")
			return new DefaultTheme64(context, resolution);
		else
			return new RunningTheme64(context, resolution);
	}

}
