package info.nightscout.androidaps.interfaces;

import java.util.List;

import info.nightscout.androidaps.data.DetailedBolusInfo;
import info.nightscout.androidaps.data.IobTotal;
import info.nightscout.androidaps.data.MealData;
import info.nightscout.androidaps.db.ExtendedBolus;
import info.nightscout.androidaps.db.ProfileSwitch;
import info.nightscout.androidaps.db.TempTarget;
import info.nightscout.androidaps.db.TemporaryBasal;
import info.nightscout.androidaps.db.Treatment;
import info.nightscout.androidaps.data.OverlappingIntervals;
import info.nightscout.androidaps.data.ProfileIntervals;

/**
 * Created by mike on 14.06.2016.
 */
public interface TreatmentsInterface {

    void updateTotalIOBTreatments();
    void updateTotalIOBTempBasals();

    IobTotal getLastCalculationTreatments();
    IobTotal getCalculationToTimeTreatments(long time);
    IobTotal getLastCalculationTempBasals();
    IobTotal getCalculationToTimeTempBasals(long time);

    MealData getMealData();

    List<Treatment> getTreatmentsFromHistory();
    List<Treatment> getTreatments5MinBackFromHistory(long time);

    // real basals (not faked by extended bolus)
    boolean isInHistoryRealTempBasalInProgress();
    TemporaryBasal getRealTempBasalFromHistory(long time);

    void addToHistoryTempBasalStart(TemporaryBasal tempBasal);
    void addToHistoryTempBasalStop(long time);

    // basal that can be faked by extended boluses
    boolean isTempBasalInProgress();
    TemporaryBasal getTempBasalFromHistory(long time);
    double getTempBasalAbsoluteRateHistory();
    double getTempBasalRemainingMinutesFromHistory();
    OverlappingIntervals<TemporaryBasal> getTemporaryBasalsFromHistory();

    boolean isInHistoryExtendedBoluslInProgress();
    ExtendedBolus getExtendedBolusFromHistory(long time);
    void addToHistoryExtendedBolusStart(ExtendedBolus extendedBolus);
    void addToHistoryExtendedBolusStop(long time);
    OverlappingIntervals<ExtendedBolus> getExtendedBolusesFromHistory();

    boolean addToHistoryTreatment(DetailedBolusInfo detailedBolusInfo);

    TempTarget getTempTargetFromHistory(long time);
    OverlappingIntervals<TempTarget> getTempTargetsFromHistory();

    ProfileSwitch getProfileSwitchFromHistory(long time);
    ProfileIntervals<ProfileSwitch> getProfileSwitchesFromHistory();
    void addToHistoryProfileSwitch(ProfileSwitch profileSwitch);

    long oldestDataAvailable();

}
