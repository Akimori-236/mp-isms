package tfip.akimori.server.repositories;

public interface MongoVariables {
    public static final String COLLECTION_STORE_ACTIVITY = "storeactivity";
    public static final String COLLECTION_USER_ACTIVITY = "useractivity";
    public static final String COLLECTION_LOAN_APPROVALS = "loanapprovals";

    public static final String ZONE_SG = "Asia/Singapore";
    public static final String TIME_FORMAT = "yyyy-LLL-dd HH:mm:ss";

    public static final String FIELD_TIME = "time";
    public static final String FIELD_STORE_ID = "store_id";
    public static final String FIELD_ACTIVITY = "activity";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_INSTRUMENT_ID = "instrument_id";
    public static final String FIELD_INSTRUMENT_TYPE = "instrument_type";
    public static final String FIELD_SERIAL_NUMBER = "serial_number";
    public static final String FIELD_ISREPAIRING = "isRepairing";
    public static final String FIELD_REMARKS = "remarks";
    public static final String FIELD_APPROVER = "approver";
    public static final String FIELD_BORROWER = "borrower";
    public static final String FIELD_EXPIREAT = "expireAt";
}
