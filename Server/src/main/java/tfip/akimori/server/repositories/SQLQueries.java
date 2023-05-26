package tfip.akimori.server.repositories;

public interface SQLQueries {
    // CREATE
    public static final String SQL_INSERT_USER = """
            INSERT INTO users(givenname, familyname, email, password, role, isGoogleLogin) VALUES (?, ?, ?, ?, ?, ?)
            """;
    public static final String SQL_INSERT_STORE = """
            INSERT INTO stores(store_id, store_name, creator_id)
            VALUES(? ,? ,(
            SELECT user_id FROM users
            WHERE email = ?));
                """; // must be executed with INSERT MANAGER
    public static final String SQL_INSERT_MANAGER = """
            INSERT INTO managers(manager_id, store_id) VALUES((
            SELECT user_id
            FROM users
            WHERE email = ?), ?)
            """; // email, store_id
    public static final String SQL_INSERT_INSTRUMENT = """
            INSERT INTO instruments(instrument_id, instrument_type, brand, model, serial_number, remarks, store_id, isRepairing)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String SQL_INSERT_GOOGLEUSER = """
            INSERT INTO google_users
            (google_user_id, email, email_verified, name, picture , family_name, given_name)
            VALUES(?, ?, ?, ?, ?, ?, ?);
            """;

    // READ
    public static final String SQL_GETUSERBYEMAIL = """
            SELECT u.email, u.givenname, u.familyname, u.password, u.role, g.picture
            FROM users u LEFT JOIN google_users g
            ON u.email = g.email
            WHERE u.email = ?
            """;
    public static final String SQL_GETMANAGEDINSTRUMENTSBYEMAIL = """
            SELECT instrument_id, instrument_type, brand, model, serial_number, store_id, store_name, isRepairing, u2.givenname, u2.familyname, u2.email
            FROM instruments i
            INNER JOIN stores s
            ON i.store_id = s.store_id
            INNER JOIN managers m
            ON m.store_id = s.store_id
            INNER JOIN users u
            ON m.manager_id = u.user_id
            LEFT JOIN users u2
            ON i.user_id = u2.user_id
            WHERE u.email = ?
            """;
    public static final String SQL_GETINSTRUMENTBYID = """
            SELECT instrument_id, instrument_type, brand, model, serial_number, i.store_id, store_name, isRepairing
            FROM instruments i
            INNER JOIN stores s
            ON i.store_id = s.store_id
            WHERE i.instrument_id = ?
            """;
    public static final String SQL_GETBORROWEDBYEMAIL = """
            SELECT instrument_id, instrument_type, brand, model, serial_number, i.store_id, store_name, isRepairing, givenname, familyname, email
            FROM instruments i
            INNER JOIN stores s
            ON i.store_id = s.store_id
            INNER JOIN users u
            ON i.user_id = u.user_id
            WHERE u.email = ?
            """;

    public static final String SQL_GETMANAGEDSTORES = """
            SELECT s.store_id, s.store_name
            FROM stores s
            INNER JOIN managers m
            ON s.store_id = m.store_id
            INNER JOIN users u
            ON m.manager_id = u.user_id
            WHERE u.email = ?
            """;

    public static final String SQL_GETALLSCHEDULES = """
            SELECT email, email_scheduled_hour
            FROM users;
            """;

    public static final String SQL_GETSTORENAME = """
            SELECT store_name FROM stores
            WHERE store_id = ?
            """;

    // public static final String SQL_GETSTOREMANAGERS = """
    // SELECT s2.store_id, store_name, email, givenname, familyname
    // FROM managers m
    // INNER JOIN
    // (SELECT s.store_id
    // FROM stores s
    // INNER JOIN managers m
    // ON s.store_id = m.store_id
    // INNER JOIN users u
    // ON m.manager_id = u.user_id
    // WHERE u.email=?) t
    // ON m.store_id = t.store_id
    // INNER JOIN stores s2
    // on s2.store_id = m.store_id
    // INNER JOIN users u2
    // on m.manager_id = u2.user_id;
    // """;

    public static final String SQL_CHECK_ISCREATOROFSTORE = """
            SELECT COUNT(*) FROM stores
            WHERE store_id = ? AND creator_id = (
            SELECT user_id FROM users
            WHERE email = ?)
            """;

    public static final String SQL_CHECK_ISMANAGEROFSTORE = """
            SELECT COUNT(*) FROM managers
            WHERE store_id = ? AND manager_id = (
            SELECT user_id FROM users
            WHERE email = ?)
            """;

    public static final String SQL_GETSTOREINSTRUMENTS = """
            SELECT instrument_id, instrument_type, brand, model, serial_number, i.store_id, store_name, isRepairing, email, givenname, familyname
            FROM instruments i
            INNER JOIN stores s
            ON i.store_id = s.store_id
            LEFT JOIN users u
            ON i.user_id = u.user_id
            WHERE s.store_id = ?
            """;

    public static final String SQL_GETSTOREMANAGERS = """
            SELECT email, givenname, familyname FROM managers m
            INNER JOIN users u
            ON m.manager_id = u.user_id
            WHERE m.store_id = ?
            """;

    public static final String SQL_GETBORROWER = """
            SELECT email FROM instruments i
            INNER JOIN users u ON i.user_id = u.user_id
            WHERE instrument_id = ?
            """;

    public static final String SQL_GETSTORENAME_BYINSTRUMENTID = """
            SELECT store_name FROM instruments i
            INNER JOIN stores s ON i.store_id = s.store_id
            WHERE instrument_id = ?
            """;

    // UPDATE
    public static final String SQL_LOANOUT_INSTRUMENT = """
            UPDATE instruments
            SET user_id = (SELECT user_id FROM users WHERE email = ?)
            WHERE instrument_id = ?
            """;
    public static final String SQL_RETURN_INSTRUMENT = """
            UPDATE instruments
            SET user_id = null
            WHERE instrument_id = ?
            """;
    public static final String SQL_UPDATE_INSTRUMENT = """
            UPDATE instruments SET
            instrument_type = ?,
            brand = ?,
            model = ?,
            serial_number = ?,
            isRepairing = ?,
            remarks = ?
            WHERE instrument_id = ?
            """;
    public static final String SQL_UPDATE_GOOGLEUSER = """
            UPDATE google_users
            SET `email` = ?,
            `email_verified` = ?,
            `name` = ?,
            `picture` = ?,
            `family_name` = ?,
            `given_name` = ?
            WHERE `google_user_id` = ?
            """;
    public static final String SQL_ISSUE_CONSUMABLE = """

            """;
    public static final String SQL_REPLENISH_CONSUMABLE = """

            """;
}