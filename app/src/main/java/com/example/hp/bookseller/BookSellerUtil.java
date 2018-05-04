package com.example.hp.bookseller;

/**
 * Created by HP on 09-08-2017.
 */

public class BookSellerUtil {

       // for user table in hostinger
       public static final String KEY_ID = "_ID";
       public static final String KEY_NAME = "NAME";
       public static final String KEY_PHONE = "PHONE";
       public static final String KEY_EMAIL = "EMAIL";
       public static final String KEY_USERNAME= "USERNAME";
       public static final String KEY_PASSWORD = "PASSWORD";
       public static final String KEY_COLLEGE = "COLLEGE_NAME";
       public static final String KEY_TRAIT = "TRAIT";
       public static final String KEY_TOKEN="TOKEN";
       public static final String KEY_UID="UID";

       // for book table in hostinger
       public static final String KEY_ITEM_ID="_ID";
       public static final String KEY_ITEM_IMAGE="ITEM_IMAGE";
       public static final String KEY_ITEM_NAME="ITEM_NAME";
       public static final String KEY_ITEM_AUTHOR="ITEM_AUTHOR";
       public static final String KEY_ITEM_PUBLISHER="ITEM_PUBLISHER";
       public static final String KEY_ITEM_CONDITION="ITEM_CONDITION";
       public static final String KEY_ITEM_PRICE="ITEM_PRICE";
       public static final String KEY_ITEM_TRAIT="ITEM_TRAIT";

       //for table in JSON format stored in Firebase
       public static final String JSON_CHAT="CHATS";
       public static final String JSON_USER="USERS";

       // for firebase notification
       public static final String ARG_FIREBASE_TOKEN = "FIREBASE_TOKEN";
       public static final String ARG_RECEIVER = "RECIVER";
       public static final String ARG_RECEIVER_UID = "RECIVER_UID";





       // for shared prefrences file
       public static final String SHAREDPREFS_LOGINFLAG="LOGINFLAG";
       public static final String SHAREDPREFS_KEYEMAIL ="KEY_EMAIL";
       public  static final String SHAREDPREFS_FILENAME="preferences_login";
       public  static final String SHAREDPREFS_SENDERTOKEN="SENDER_TOKEN";
       public  static final String SHAREDPREFS_SENDERPASSWORD="SENDER_PASSWORD";


       // links to all our URL's
       public static final String URL_INSERT="https://taniaanand31.000webhostapp.com/bookdig/insert.php";
       public static final String URL_LOGINCHECK="https://taniaanand31.000webhostapp.com/bookdig/loginQuery.php";
       public static final String URL_RETRIEVE="https://taniaanand31.000webhostapp.com/bookdig/retrieveRegistration.php";
       public static final String URL_UPDATE="https://taniaanand31.000webhostapp.com/bookdig/update.php";
       public static final String URL_ITEMINSERT="https://taniaanand31.000webhostapp.com/bookdig/insertitem.php";
       public static final String URL_DELETE_ITEM="https://taniaanand31.000webhostapp.com/bookdig/deleteUserItem.php";
       public static final String URL_RETRIEVE_ITEM="https://taniaanand31.000webhostapp.com/bookdig/retrieveItem.php";
       public static final String URL_RETRIEVE_USERITEM="https://taniaanand31.000webhostapp.com/bookdig/retrieveUserItems.php";
       public static final String URL_RETRIEVE_ALLITEMS="https://taniaanand31.000webhostapp.com/bookdig/retrieveALL.php";
       public static final String URL_UPDATE_ITEM="https://taniaanand31.000webhostapp.com/bookdig/updateItem.php";
       public static final String URL_RETRIEVE_PHONENO="https://taniaanand31.000webhostapp.com/bookdig/rPhoneNo.php";


}
