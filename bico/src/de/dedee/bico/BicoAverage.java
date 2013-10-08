package de.dedee.bico;

public class BicoAverage {

	private static double[] last6Distance = new double[6];
	private static long[] last6Time = new long[6];
	private static int indexupdate;

	public static void ClearPoints() {
		for (int index = 0; index < 6; index++) {
			last6Distance[index] = 0;
			last6Time[index] = 0;
		}
		indexupdate = 0;
	}

	public static double GetAverageSpeedOverOneMinute(double TotalDistance, long TotalTime) {
		double TotalDistance6TimesAgo = last6Distance[indexupdate];
		long TotalTime6TimesAgo = last6Time[indexupdate];

		last6Distance[indexupdate] = TotalDistance;
		last6Time[indexupdate] = TotalTime;

		indexupdate++;
		if (indexupdate == 6)
			indexupdate = 0;

		if (TotalTime - TotalTime6TimesAgo == 0) // no move over last minute
			return (0.);
		else
			return (TotalTime6TimesAgo == 0 ? -1. : (TotalDistance - TotalDistance6TimesAgo)
					/ ((TotalTime - TotalTime6TimesAgo) / 1000));
	}

}
