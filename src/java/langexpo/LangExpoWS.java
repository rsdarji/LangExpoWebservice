/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package langexpo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author UTILISATEUR
 */
@Path("webservices")
public class LangExpoWS {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of LangExpoWS
     */
    public LangExpoWS() {
    }

    /**
     * Retrieves representation of an instance of langexpo.LangExpoWS
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of LangExpoWS
     * @param content representation for the resource
     */
    @PUT
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    private long getPrimaryKeyforTable(String tableName, String columnName) throws SQLException{
    
        Connection con = null;
        Statement stm = null;
        ResultSet rs=null;
        long pk=0;
        try{
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
            stm = con.createStatement();
            rs = stm.executeQuery("select "+columnName+" from "+tableName+" order by "+columnName+" DESC");
            if(rs.next()==true){
                pk=rs.getInt(columnName);
                System.out.println("pk: "+pk);
                pk=++pk;
                System.out.println("pk : after: "+pk);
            }else{
                pk = 1;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }finally{
            rs.close();
            stm.close();
            con.close();
        }
        return pk;
    }
    
    @GET
    @Path("login&{username}&{password}")
    @Produces("application/json")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String login(@PathParam("username") String username, @PathParam("password") String password) throws SQLException, JSONException{
        JSONObject user = new JSONObject();
        Statement stm = null;
        Connection con = null;
        ResultSet rs = null;
        System.out.println("username: "+username+"  password: "+password);
            if(username!="" | password.trim()!=""){
            //username!=null | password!=null !username.equalsIgnoreCase("")| !password.equalsIgnoreCase("")
            try{
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
                stm = con.createStatement();
                String sql = "select * from user_ where email='"+username+"' and password='"+password+"'";
                rs = stm.executeQuery(sql);

                int userId = 0;
                while (rs.next()) {
                    userId = rs.getInt("user_id");

                    user.accumulate("status", "ok");
                    user.accumulate("time", System.currentTimeMillis());
                    user.accumulate("user_id", userId);
                    user.accumulate("first_name",rs.getString("first_name"));
                    user.accumulate("last_name",rs.getString("last_name"));
                    user.accumulate("email",rs.getString("email"));
                    user.accumulate("contact",rs.getString("phone"));
//                    user.accumulate("address",rs.getString("address"));
//                    user.accumulate("date_of_birth",rs.getString("dateofbirth"));
//                    user.accumulate("gender",rs.getString("gender"));
//                    user.accumulate("usertype",rs.getInt("usertype"));

                }
                
                rs.close();
                
            }catch(SQLException e){
                e.printStackTrace();
                user.accumulate("status", "error");
                user.accumulate("message", "Please check your details and try again.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }finally{
                stm.close();
                con.close();
            }
        }else{
            user.accumulate("status", "error");
        }
        return user.toString();
    }
    
    
    
    @GET
    @Path("featchAllLanguages")
    @Produces("application/json")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String featchAllLanguages() throws SQLException, JSONException{
        JSONObject object = new JSONObject();
        
        
        Statement stm = null;
        Connection con = null;
        ResultSet rs = null;
           
            try{
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
                stm = con.createStatement();
                String sql = "Select LANGUAGE_ID, LANGUAGE_NAME, Image.Image " +
                    "from Language_ Inner join IMAGE ON Language_.FLAG_ID = Image.IMAGE_ID order by MODIFIED_DATE DESC";
                
                rs = stm.executeQuery(sql);
                
                if(rs.next()==true){
                    object.accumulate("status", "ok");
                    object.accumulate("message","Featched up successfully.");
                    JSONArray languages = new JSONArray();
                    do {
                        JSONObject language = new JSONObject();
                        language.accumulate("languageId", rs.getLong("LANGUAGE_ID"));
                        language.accumulate("languageName", rs.getString("LANGUAGE_NAME"));
                        language.accumulate("languageFlagURL", rs.getString("IMAGE"));
                        languages.put(language);
                    } while (rs.next());
                    object.accumulate("languages", languages);
                }else{
                    object.accumulate("status", "error");
                    object.accumulate("message", "Please try again.");
                }
               
                rs.close();
                
            }catch(SQLException e){
                e.printStackTrace();
                object.accumulate("status", "error");
                object.accumulate("message", "Please try again.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }finally{
                stm.close();
                con.close();
            }
        
        return object.toString();
    }
    
    @POST
    @Path("addUpdateLanguage")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUpdateLanguage(@FormParam("languageId") long languageId,
            @FormParam("languageFlagURL") String languageFlagURL,
            @FormParam("languageName") String languageName) throws SQLException, JSONException {
        
        languageFlagURL = languageFlagURL.replace("images/", "images%2F");
        
        JSONObject addUpdateLanguage = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int imageCount = 0;
        int languageCount = 0;
        long imageId = 0;
        
        System.out.println("languageFlagURL: "+languageFlagURL+" \n languageName: "+languageName);
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            //update
            if(languageId!=0){
                String sql = "SELECT * FROM LANGUAGE_ WHERE LANGUAGE_ID="+languageId;
                rs = stmt.executeQuery(sql);
                while(rs.next()){
                    imageId = rs.getLong("FLAG_ID");
                 }
                 rs.close();
                sql = "UPDATE LANGUAGE_ SET LANGUAGE_NAME = '"+languageName+"', MODIFIED_DATE=CURRENT_TIMESTAMP WHERE LANGUAGE_ID = "+languageId;
                languageCount = stmt.executeUpdate(sql);
                
                if(languageCount==1){
                    
                    
                    sql = "UPDATE IMAGE SET IMAGE = '"+languageFlagURL+"', MODIFIED_DATE=CURRENT_TIMESTAMP WHERE IMAGE_ID = "+imageId;
                    imageCount = stmt.executeUpdate(sql);
                    if (imageCount==1) {
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Language has been updated successfully.");
                    }else {
                        addUpdateLanguage.accumulate("status", "error");
                        addUpdateLanguage.accumulate("message", "Language has not been added.");
                    }
                    
                }
                

            } else{ //add
                long imagePK = getPrimaryKeyforTable("IMAGE", "IMAGE_ID");
                long languagePK = getPrimaryKeyforTable("LANGUAGE_", "LANGUAGE_ID");

                String sql = "INSERT INTO A19MADTEAM5.IMAGE (IMAGE_ID, IMAGE, CREATED_DATE, MODIFIED_DATE) " +
                        "VALUES ("+imagePK+",'"+languageFlagURL+"',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                imageCount = stmt.executeUpdate(sql);

                if (imageCount==1) {
                    imageId = imagePK;
                }

                if(imageId!=0){
                    sql = "INSERT INTO A19MADTEAM5.LANGUAGE_ (LANGUAGE_ID, LANGUAGE_NAME, FLAG_ID, CREATED_DATE, MODIFIED_DATE) " +
                        "VALUES ("+languagePK+", '"+languageName+"', "+imageId+", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

                    languageCount = stmt.executeUpdate(sql);

                    if(languageCount==1) {
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Language has been added successfully.");
                    }
                    else{
                        addUpdateLanguage.accumulate("status", "error");
                        addUpdateLanguage.accumulate("message", "Language has not been added.");
                    }
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
            addUpdateLanguage.accumulate("status", "error");
            addUpdateLanguage.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateLanguage.toString();

    }
    
    @POST
    @Path("deleteLanguage")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String deleteLanguage(@FormParam("languageId") long languageId) throws SQLException, JSONException {
        
        JSONObject addUpdateLanguage = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int imageCount = 0;
        int languageCount = 0;
        long imageId = 0;
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            //update
            if(languageId!=0){
                String sql = "SELECT * FROM LANGUAGE_ WHERE LANGUAGE_ID="+languageId;
                rs = stmt.executeQuery(sql);
                while(rs.next()){
                    imageId = rs.getLong("FLAG_ID");
                 }
                 rs.close();
                sql = "DELETE FROM LANGUAGE_ WHERE LANGUAGE_ID="+languageId;
                languageCount = stmt.executeUpdate(sql);
                
                if(languageCount==1){
                    
                    
                    sql = "DELETE FROM IMAGE WHERE IMAGE_ID = "+imageId; 
                    imageCount = stmt.executeUpdate(sql);
                    if (imageCount==1) {
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Language has been deleted successfully.");
                    }else {
                        addUpdateLanguage.accumulate("status", "error");
                        addUpdateLanguage.accumulate("message", "Language has not been deleted.");
                    }
                    
                }
            } else{ //add
                addUpdateLanguage.accumulate("status", "error");
                addUpdateLanguage.accumulate("message", "You have not selected Language to delete.");
            }

        }catch(SQLException e){
            e.printStackTrace();
            addUpdateLanguage.accumulate("status", "error");
            addUpdateLanguage.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateLanguage.toString();

    }
    
    //for reference of get request, current this method is not in use.
    @GET
    @Path("addLanguage&{languageFlagURL}&{languageName}")
    @Produces("application/json")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addLanguage(@PathParam("languageFlagURL") String languageFlagURL, @PathParam("languageName") String languageName) throws SQLException, JSONException{
        JSONObject user = new JSONObject();
        Statement stm = null;
        Connection con = null;
        ResultSet rs = null;
        System.out.println("languageFlagURL: "+languageFlagURL+" \n languageName: "+languageName);
           
            try{
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
                stm = con.createStatement();
                
                
                long imagePK = getPrimaryKeyforTable("IMAGE", "IMAGE_ID");
                long languagePK = getPrimaryKeyforTable("LANGUAGE_", "LANGUAGE_ID");
                long imageId = 0;
                String sql = "INSERT INTO A19MADTEAM5.IMAGE (IMAGE_ID, IMAGE, CREATED_DATE, MODIFIED_DATE) " +
                        "VALUES ("+imagePK+",'"+languageFlagURL+"',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                rs = stm.executeQuery(sql);
                
                while (rs.next()) {
                    imageId = rs.getInt("IMAGE_ID");
                }
                
                if(imageId!=0){
                    sql = "INSERT INTO A19MADTEAM5.LANGUAGE_ (LANGUAGE_ID, LANGUAGE_NAME, FLAG_ID, CREATED_DATE, MODIFIED_DATE) " +
                        "VALUES ("+languagePK+", '"+languageName+"', "+imageId+", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                    
                    rs = stm.executeQuery(sql);
                    
                    if(rs.next()==true) {
                        user.accumulate("status", "ok");
                        user.accumulate("message","Language has been added successfully.");
                    }
                    else{
                        user.accumulate("status", "error");
                        user.accumulate("message", "Language has not been added.");
                    }
                }
                
                rs.close();
                
            }catch(SQLException e){
                e.printStackTrace();
                user.accumulate("status", "error");
                user.accumulate("message", "Please check details and try again.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }finally{
                stm.close();
                con.close();
            }
        
        return user.toString();
    }
   
    
    
    @GET
    @Path("featchAlllevel")
    @Produces("application/json")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String featchAlllevel() throws SQLException, JSONException{
        JSONObject object = new JSONObject();
        
        
        Statement stm = null;
        Connection con = null;
        ResultSet rs = null;
           
            try{
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
                stm = con.createStatement();
                String sql = "select * from COURSE_LEVEL order by MODIFIED_DATE DESC";
                
                rs = stm.executeQuery(sql);
                
                if(rs.next()==true){
                    
                    JSONArray levels = new JSONArray();
                    do {
                        JSONObject level = new JSONObject();
                        level.accumulate("levelId", rs.getLong("LEVEL_ID"));
                        level.accumulate("levelName", rs.getString("LEVEL_NAME"));
                        level.accumulate("levelType", rs.getString("LEVEL_TYPE"));
                        level.accumulate("sequenceNumber", rs.getInt("SEQUENCE_NUMBER"));
                        levels.put(level);
                    } while (rs.next());
                    object.accumulate("levels", levels);
                    object.accumulate("status", "ok");
                    object.accumulate("message","Featched up successfully.");
                }else{
                    object.accumulate("status", "error");
                    object.accumulate("message", "Please try again.");
                }
               
                rs.close();
                
            }catch(SQLException e){
                e.printStackTrace();
                object.accumulate("status", "error");
                object.accumulate("message", "Please try again.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            finally{
                stm.close();
                con.close();
            }
        
        return object.toString();
    }
            
    @POST
    @Path("addUpdateLevel")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUpdateLevel(@FormParam("levelId") long levelId, @FormParam("levelNameValue") String levelNamne,
            @FormParam("userLevelValue") String userLevel, @FormParam("sequenceNumberValue") int sequenceNumber) throws SQLException, JSONException {
        
        JSONObject addUpdateLanguage = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int imageCount = 0;
        int levelCount = 0;
        long imageId = 0;
        int duplicateCount = 0;
      
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            String sql = "SELECT * FROM COURSE_LEVEL WHERE SEQUENCE_NUMBER = "+sequenceNumber+" AND LEVEL_TYPE = '"+userLevel+"'";
            duplicateCount = stmt.executeUpdate(sql);
            //DUPLICAT
            if(duplicateCount==0){
            
                //update
                if(levelId!=0){

                    sql = "UPDATE COURSE_LEVEL SET LEVEL_NAME = '"+levelNamne+"', LEVEL_TYPE = '"+userLevel+"',"+
                            " SEQUENCE_NUMBER = "+sequenceNumber+", MODIFIED_DATE=CURRENT_TIMESTAMP WHERE LEVEL_ID = "+levelId;
                    levelCount = stmt.executeUpdate(sql);

                    if(levelCount==1){
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Level has been updated successfully.");
                    }


                } else{ //add
                    long levelPK = getPrimaryKeyforTable("COURSE_LEVEL", "LEVEL_ID");
                    
                    sql = "INSERT INTO A19MADTEAM5.COURSE_LEVEL (LEVEL_ID, LEVEL_NAME, LEVEL_TYPE, "+
                            "SEQUENCE_NUMBER, CREATED_DATE, MODIFIED_DATE) VALUES ("+levelPK+", '"+levelNamne+"','"+
                            userLevel+"', "+sequenceNumber+", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                    levelCount = stmt.executeUpdate(sql);

                    if (levelCount==1) {
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Level has been added successfully.");
                    }
                    else{
                        addUpdateLanguage.accumulate("status", "error");
                        addUpdateLanguage.accumulate("message", "Level has not been added.");
                    }
                }
            }else{
                addUpdateLanguage.accumulate("status", "error");
                addUpdateLanguage.accumulate("code", "LE_D_411");
                addUpdateLanguage.accumulate("message", "Please check details and try again.");
            }
        }catch(SQLException e){
            e.printStackTrace();
            addUpdateLanguage.accumulate("status", "error");
            addUpdateLanguage.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateLanguage.toString();

    }
    
    
    @POST
    @Path("deleteLevel")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String deleteLevel(@FormParam("levelId") long levelId) throws SQLException, JSONException {
        
        JSONObject addUpdateLanguage = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int levelCount = 0;
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            if(levelId!=0){
                try{
                    String sql = "DELETE FROM COURSE_LEVEL WHERE LEVEL_ID="+levelId;
                    levelCount = stmt.executeUpdate(sql);

                    if(levelCount==1){
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Level has been deleted successfully.");
                    }
                }catch(SQLIntegrityConstraintViolationException e){
                    addUpdateLanguage.accumulate("status", "error");
                    addUpdateLanguage.accumulate("message", "Level is in use, you can't delete it.");
                }
            } else{ //add
                addUpdateLanguage.accumulate("status", "error");
                addUpdateLanguage.accumulate("message", "You have not selected Level to delete.");
            }

        }catch(SQLException e){
            e.printStackTrace();
            addUpdateLanguage.accumulate("status", "error");
            addUpdateLanguage.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateLanguage.toString();

    }
    
    @GET
    @Path("featchAllQuestionType")
    @Produces("application/json")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String featchAllQuestionType() throws SQLException, JSONException{
        JSONObject object = new JSONObject();
        
        
        Statement stm = null;
        Connection con = null;
        ResultSet rs = null;
           
            try{
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
                stm = con.createStatement();
                String sql = "select * from QUESTION_TYPE order by MODIFIED_DATE DESC";
                
                rs = stm.executeQuery(sql);
                
                if(rs.next()==true){
                    object.accumulate("status", "ok");
                    object.accumulate("message","Featched up successfully.");
                    JSONArray questionTypes = new JSONArray();
                    do {
                        JSONObject questionType = new JSONObject();
                        questionType.accumulate("questionTypeId", rs.getLong("question_type_id"));
                        questionType.accumulate("questionTypeName", rs.getString("question_type_name"));
                        questionType.accumulate("totalOptions", rs.getInt("total_options"));
                        questionType.accumulate("totalDisplayOptions", rs.getInt("total_display_options"));
                        questionType.accumulate("multipleAnswer", rs.getBoolean("multiple_answer"));
                        questionType.accumulate("questionAudio", rs.getBoolean("question_audio"));
                        questionType.accumulate("optionAudio", rs.getBoolean("option_audio"));
                        questionType.accumulate("questionImage", rs.getBoolean("question_image"));
                        questionType.accumulate("optionImage", rs.getBoolean("option_image"));
                        questionTypes.put(questionType);
                    } while (rs.next());
                    object.accumulate("questionTypes", questionTypes);
                }else{
                    object.accumulate("status", "error");
                    object.accumulate("message", "Please try again.");
                }
               
                rs.close();
                
            }catch(SQLException e){
                e.printStackTrace();
                object.accumulate("status", "error");
                object.accumulate("message", "Please try again.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }finally{
                stm.close();
                con.close();
            }
        
        return object.toString();
    }
    
    @POST
    @Path("addUpdateQuestionType")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUpdateQuestionType(@FormParam("questionTypeId") long questionTypeId, @FormParam("questionTypeNameValue") String questionTypeNameValue,
            @FormParam("totalOptionsValue") int totalOptionsValue, @FormParam("totalDisplayOptionsValue") int totalDisplayOptionsValue,
            @FormParam("multipleAnswerValue") boolean multipleAnswerValue, @FormParam("questionAudioValue") boolean questionAudioValue,
            @FormParam("optionAudioValue") boolean optionAudioValue, @FormParam("questionImageValue") boolean questionImageValue,
            @FormParam("optionImageValue") boolean optionImageValue) throws SQLException, JSONException {
        
        JSONObject addUpdateLanguage = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int imageCount = 0;
        int questionTypeCount = 0;
        long imageId = 0;
        
        System.out.println("questionTypeId: "+questionTypeId+" \n questionTypeNameValue: "+questionTypeNameValue+"\n"+
                "totalOptionsValue: "+totalOptionsValue+" \n totalDisplayOptionsValue: "+totalDisplayOptionsValue+"\n"+
                "multipleAnswerValue: "+Utility.booleanToInt(multipleAnswerValue)+" \n questionAudioValue: "+Utility.booleanToInt(questionAudioValue)+"\n"+
                "optionAudioValue: "+Utility.booleanToInt(optionAudioValue)+" \n questionImageValue: "+Utility.booleanToInt(questionImageValue)+"\n"+
                "optionImageValue: "+Utility.booleanToInt(optionImageValue));
        
        int multipleAnswerValueInt = Utility.booleanToInt(multipleAnswerValue);
        int questionAudioValueInt = Utility.booleanToInt(questionAudioValue);
        int optionAudioValueInt = Utility.booleanToInt(optionAudioValue);
        int questionImageValueInt = Utility.booleanToInt(questionImageValue);
        int optionImageValueInt = Utility.booleanToInt(optionImageValue);
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            //update
            if(questionTypeId!=0){
                
                
                String sql = "UPDATE QUESTION_TYPE SET question_type_name = '"+questionTypeNameValue+"', total_options = "+totalOptionsValue+","+
                        " total_display_options = "+totalDisplayOptionsValue+", multiple_answer = "+multipleAnswerValueInt+","+
                        " question_audio = "+questionAudioValueInt+", option_audio = "+optionAudioValueInt+","+
                        " question_image = "+questionImageValueInt+", option_image = "+optionImageValueInt+","+
                        " MODIFIED_DATE=CURRENT_TIMESTAMP WHERE question_type_id = "+questionTypeId;
                questionTypeCount = stmt.executeUpdate(sql);
                
                if(questionTypeCount==1){
                    addUpdateLanguage.accumulate("status", "ok");
                    addUpdateLanguage.accumulate("message","Question Type has been updated successfully.");
                }
                

            } else{ //add
                long questionTypePK = getPrimaryKeyforTable("QUESTION_TYPE", "QUESTION_TYPE_ID");
                long languagePK = getPrimaryKeyforTable("LANGUAGE_", "LANGUAGE_ID");

                
                String sql = "INSERT INTO A19MADTEAM5.QUESTION_TYPE (QUESTION_TYPE_ID, QUESTION_TYPE_NAME, TOTAL_OPTIONS, "+
                        "TOTAL_DISPLAY_OPTIONS, MULTIPLE_ANSWER, QUESTION_AUDIO, OPTION_AUDIO, QUESTION_IMAGE, "+
                        "OPTION_IMAGE, CREATED_DATE, MODIFIED_DATE) VALUES ("+questionTypePK+", '"+questionTypeNameValue+"',"+
                        totalOptionsValue+", "+totalDisplayOptionsValue+", "+multipleAnswerValueInt+", "+questionAudioValueInt+","+
                        optionAudioValueInt+", "+questionImageValueInt+", "+optionImageValueInt+", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                questionTypeCount = stmt.executeUpdate(sql);

                if (questionTypeCount==1) {
                    addUpdateLanguage.accumulate("status", "ok");
                    addUpdateLanguage.accumulate("message","Question Type has been added successfully.");
                }
                else{
                    addUpdateLanguage.accumulate("status", "error");
                    addUpdateLanguage.accumulate("message", "Question Type has not been added.");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            addUpdateLanguage.accumulate("status", "error");
            addUpdateLanguage.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateLanguage.toString();

    }
    
    @POST
    @Path("deleteQuestionType")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String deleteQuestionType(@FormParam("questionTypeId") long questionTypeId) throws SQLException, JSONException {
        
        JSONObject addUpdateLanguage = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int imageCount = 0;
        int questionTypeCount = 0;
        long imageId = 0;
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            //update
            if(questionTypeId!=0){
                try{
                    String sql = "DELETE FROM QUESTION_TYPE WHERE QUESTION_TYPE_ID="+questionTypeId;
                    questionTypeCount = stmt.executeUpdate(sql);

                    if(questionTypeCount==1){
                        addUpdateLanguage.accumulate("status", "ok");
                        addUpdateLanguage.accumulate("message","Question type has been deleted successfully.");
                    }
                }catch(SQLIntegrityConstraintViolationException e){
                    addUpdateLanguage.accumulate("status", "error");
                    addUpdateLanguage.accumulate("message", "Question type is in use, you can't delete it.");
                }
            } else{ //add
                addUpdateLanguage.accumulate("status", "error");
                addUpdateLanguage.accumulate("message", "You have not selected Language to delete.");
            }

        }catch(SQLException e){
            e.printStackTrace();
            addUpdateLanguage.accumulate("status", "error");
            addUpdateLanguage.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateLanguage.toString();

    }
    
    @GET
    @Path("featchAllGoal")
    @Produces("application/json")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String featchAllGoal() throws SQLException, JSONException{
        JSONObject object = new JSONObject();
        
        
        Statement stm = null;
        Connection con = null;
        ResultSet rs = null;
           
            try{
                Class.forName("oracle.jdbc.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "a19madteam5", "anypw");
                stm = con.createStatement();
                String sql = "select * from GOAL_MASTER order by MODIFIED_DATE DESC";
                
                rs = stm.executeQuery(sql);
                
                if(rs.next()==true){
                    object.accumulate("status", "ok");
                    object.accumulate("message","Featched up successfully.");
                    JSONArray goals = new JSONArray();
                    do {
                        JSONObject goal = new JSONObject();
                        goal.accumulate("goalId", rs.getLong("GOAL_MASTER_ID"));
                        goal.accumulate("goalName", rs.getString("GOAL_NAME"));
                        
                        goals.put(goal);
                    } while (rs.next());
                    object.accumulate("goals", goals);
                }else{
                    object.accumulate("status", "error");
                    object.accumulate("message", "Please try again.");
                }
               
                rs.close();
                
            }catch(SQLException e){
                e.printStackTrace();
                object.accumulate("status", "error");
                object.accumulate("message", "Please try again.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }finally{
                stm.close();
                con.close();
            }
        
        return object.toString();
    }
    
    @POST
    @Path("addUpdateGoal")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUpdateGoal(@FormParam("goalId") long goalId, @FormParam("goalNameValue") String goalName) throws SQLException, JSONException {
        
        JSONObject addUpdateGoal = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int goalCount = 0;
        int duplicateCount = 0;
        
        System.out.println("goalId: "+goalId+" \n goalName: "+goalName+"\n");
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            String sql = "SELECT * FROM GOAL_MASTER WHERE GOAL_NAME = '"+goalName+"'";
            duplicateCount = stmt.executeUpdate(sql);
            //DUPLICAT
            if(duplicateCount==0){
                //update
                if(goalId!=0){

                    sql = "UPDATE GOAL_MASTER SET GOAL_NAME = '"+goalName+"', MODIFIED_DATE=CURRENT_TIMESTAMP WHERE GOAL_MASTER_ID = "+goalId;
                    goalCount = stmt.executeUpdate(sql);

                    if(goalCount==1){
                        addUpdateGoal.accumulate("status", "ok");
                        addUpdateGoal.accumulate("message","Goal Master has been updated successfully.");
                    }

                } else{ //add
                    long goalPK = getPrimaryKeyforTable("GOAL_MASTER", "GOAL_MASTER_ID");


                    sql = "INSERT INTO A19MADTEAM5.GOAL_MASTER (GOAL_MASTER_ID, GOAL_NAME,"
                            + " CREATED_DATE, MODIFIED_DATE) VALUES ("+goalPK+", '"+goalName+"', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                    goalCount = stmt.executeUpdate(sql);

                    if (goalCount==1) {
                        addUpdateGoal.accumulate("status", "ok");
                        addUpdateGoal.accumulate("message","Goal has been added successfully.");
                    }
                    else{
                        addUpdateGoal.accumulate("status", "error");
                        addUpdateGoal.accumulate("message", "Goal has not been added.");
                    }
                }
            }else{
                addUpdateGoal.accumulate("status", "error");
                addUpdateGoal.accumulate("code", "LE_D_411");
                addUpdateGoal.accumulate("message", "Please check details and try again.");
            }
        }catch(SQLException e){
            e.printStackTrace();
            addUpdateGoal.accumulate("status", "error");
            addUpdateGoal.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return addUpdateGoal.toString();

    }
    
    @POST
    @Path("deleteGoal")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String deleteGoal(@FormParam("goalId") long goalId) throws SQLException, JSONException {
        
        JSONObject deleteGoal = new JSONObject();
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int goalCount = 0;
        
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "A19MADTEAM5", "anypw");
            stmt = con.createStatement();
            
            //update
            if(goalId!=0){
                try{
                    String sql = "DELETE FROM GOAL_MASTER WHERE GOAL_MASTER_ID="+goalId;
                    goalCount = stmt.executeUpdate(sql);

                    if(goalCount==1){
                        deleteGoal.accumulate("status", "ok");
                        deleteGoal.accumulate("message","Goal has been deleted successfully.");
                    }
                }catch(SQLIntegrityConstraintViolationException e){
                    deleteGoal.accumulate("status", "error");
                    deleteGoal.accumulate("message", "Goal is in use, you can't delete it.");
                }
            } else{ //add
                deleteGoal.accumulate("status", "error");
                deleteGoal.accumulate("message", "You have not selected Goal to delete.");
            }

        }catch(SQLException e){
            e.printStackTrace();
            deleteGoal.accumulate("status", "error");
            deleteGoal.accumulate("message", "Please check details and try again.");
        }catch(ClassNotFoundException e){
            Logger.getLogger(LangExpoWS.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            stmt.close();
            con.close();
        }
        
        return deleteGoal.toString();

    }
    
}        
            
   
