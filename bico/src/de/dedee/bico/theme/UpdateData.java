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

public class UpdateData {

	private String title;
	private UpdateValue averageSpeed;
	private UpdateValue movingTime;
	private UpdateValue totalTime;
	private UpdateValue totalTimeRounded;
	private UpdateValue totalDistance;
	private UpdateValue elevationGain;
	private UpdateValue averageSpeedOverOneMinute;
	private int averageTrend;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UpdateValue getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(UpdateValue averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public UpdateValue getAverageSpeedOverOneMinute() {
		return averageSpeedOverOneMinute;
	}

	public void setAverageSpeedOverOneMinute(UpdateValue averageSpeedOverOneMinute) {
		this.averageSpeedOverOneMinute = averageSpeedOverOneMinute;
	}

	public UpdateValue getMovingTime() {
		return movingTime;
	}

	public void setMovingTime(UpdateValue movingTime) {
		this.movingTime = movingTime;
	}

	public UpdateValue getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(UpdateValue totalTime) {
		this.totalTime = totalTime;
	}

	public UpdateValue getTotalTimeRounded() {
		return totalTimeRounded;
	}

	public void setTotalTimeRounded(UpdateValue totalTimeRounded) {
		this.totalTimeRounded = totalTimeRounded;
	}

	public UpdateValue getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(UpdateValue totalDistance) {
		this.totalDistance = totalDistance;
	}

	public int getAverageTrend() {
		return averageTrend;
	}

	public void setAverageTrend(int averageTrend) {
		this.averageTrend = averageTrend;
	}

	public UpdateValue getElevationGain() {
		return elevationGain;
	}

	public void setElevationGain(UpdateValue elevationGain) {
		this.elevationGain = elevationGain;
	}

	@Override
	public String toString() {
		return "UpdateData [title=" + title + ", averageSpeed=" + averageSpeed + ", movingTime=" + movingTime
				+ ", elevationGain=" + elevationGain + "]";
	}

}