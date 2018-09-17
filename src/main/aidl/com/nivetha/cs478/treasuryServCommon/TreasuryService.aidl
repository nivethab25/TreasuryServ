// TreasuryService.aidl
package com.nivetha.cs478.treasuryServCommon;

interface TreasuryService {

   int[] monthlyCash(int year);

   int[] dailyCash(int day, int month, int year, int workingDays);

   int yearlyAvg(int year);
}
