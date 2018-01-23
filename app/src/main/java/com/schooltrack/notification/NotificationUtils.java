package com.schooltrack.notification;

import android.content.Context;
import android.support.v4.app.NotificationCompat;


public class NotificationUtils {
	private static NotificationUtils mContext;
	

	public static NotificationUtils getInstance() {
		if (mContext == null) {
			mContext = new NotificationUtils();
		}
		return mContext;
	}

	/**
	 * Create the request format and called makeFavoriteFromNotification method
	 */
/*	public void favorite(final Context context,final String storyID, final String type, final boolean comingFromNotification, String headLine, String name,final String rssURL,final String ceartionDate2) {
		try {
			final IBNLiveDatabaseManager obj = new IBNLiveDatabaseManager(context);
			String tableName = DataBaseFields.FAVOURITE_TABLE;
		   // long dateInMilliSecond=	convertToMillisecond(ceartionDate2);
			final	JSONObject jSon=new JSONObject();   
			jSon.put("name",headLine);   
			jSon.put("story_id",storyID);   
			jSon.put("creation_date2","");   
			jSon.put("headline",headLine);   
			jSon.put("type",type);  
			tableName = tableName + "_HINDI";
			Log.i("FAVORITE", "FAVORITE");
			obj.makeFavoriteFromNotification(context.getApplicationContext(), storyID, true, type, comingFromNotification,jSon.toString(),rssURL,"");
			if (type!=null && (type.equalsIgnoreCase("news")||type.equalsIgnoreCase("videos"))) {
				saveNewsDetailIntoDataBase(context.getApplicationContext(), storyID, rssURL, type);
			} else if (type!=null && (type.equalsIgnoreCase("photos"))) {
				savePhotosDetailIntoDataBase(context.getApplicationContext(), storyID, rssURL, "");
			}else  if (type!=null && (type.equalsIgnoreCase("blogs"))) {
				saveBlogsDetailIntoDataBase(context.getApplicationContext(), storyID, rssURL);
			} 

		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveNewsDetailIntoDataBase(final Context context,final String storyID,final String rssURL,final String type){
		new Thread() {
			@Override
			public void run() {
				try {
					NewsDetailEntity newsDetailEntity = Parse.getInstance().getNewsDetailDataFromDb(storyID, context, rssURL, type);//"news");
					if (newsDetailEntity==null) {
						Parse.getInstance().getNewsDetailData(storyID, context, rssURL, type,true);//"news");	
					} else {
					}

				} catch (Exception e) {
					e.printStackTrace();

				} finally {
				}
			};
		}.start();
	}

	public void saveBlogsDetailIntoDataBase(final Context context,final String storyID,final String rssURL){
		new Thread() {
			@Override
			public void run() {
				try {
					final NewsOfflineDB obj = new IBNLiveDatabaseManager(context).getBlogDetailFromDB(storyID);
					final BlogDetailParser parseResult = new BlogDetailParser();
					if (obj != null && obj.getDetailJSON() != null) {
						return ;
					} else {
						final ParserUtil parUtil = new ParserUtil(context);
						if (Utils.getInstance().isOnline(context)) {
							final String respStr = parUtil.fetchJSONResponse(rssURL);
							if (respStr != null) {
								BlogIndividualEntity newsdetail = parseResult.parseNewsDetailData(context, respStr);
								if (newsdetail != null && newsdetail.getContent_id() != null && !newsdetail.getContent_id().equalsIgnoreCase(""))
									Parse.getInstance().saveBlogDetailTODatabase(context, newsdetail, respStr, rssURL,true);
							} else {
							}
						}
					}


				} catch (Exception e) {
					e.printStackTrace();

				} finally {
				}
			};
		}.start();
	}

	public void savePhotosDetailIntoDataBase(final Context context,final String storyID,final String rssURL,final String creationDate){
		new Thread() {
			@Override
			public void run() {
				try {
					PhotoDetailEntity photoDetailEntity = Parse.getInstance().getPhotoDetailDataFromDb(storyID, context, rssURL);
					if (photoDetailEntity == null) {
						photoDetailEntity = Parse.getInstance().getPhotoDetailData(storyID, context, rssURL, "",creationDate,true);
					} else {
					}
				} catch (Exception e) {
					e.printStackTrace();

				} finally {
				}
			};
		}.start();
	}*/
	
	public NotificationCompat.BigTextStyle getBigTexNotificationStyle(String message){
	/*	NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
		notiStyle.bigText(message);
	*/	return null;
	}

	public void getBigPictureNotificationStyle(String message, String bigImageURL){
	/*	NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
		bigPictureStyle.setBigContentTitle("First Post");
		bigPictureStyle.setSummaryText(message);
		String sample_url = bigImageURL;//"http://codeversed.com/androidifysteve.png";
		Bitmap remote_picture;
		try {
			remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		bigPictureStyle.bigPicture(remote_picture);
		return bigPictureStyle;
	*/
	}
	
	/**
	 * Convert date format to millisecond
	 */
	public void convertToMillisecond(String stringDate){
	/*	long dateInMilli=System.currentTimeMillis();
		try {
			String someDate = "yy-MM-dd HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(someDate);
			Date date = sdf.parse(stringDate);
			dateInMilli=date.getTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateInMilli/1000;
	*/
		}
	
	public void saveDataIntoDB(Context context,String storyID,String rssURL,String type,String headline) {
	/*	try{
			if(Utils.getInstance().isOnline(context)){
				if(type.equalsIgnoreCase("news")){
					saveNewsDetailIntoDB(context, storyID, rssURL, type);
				}else if(type.equalsIgnoreCase("photo") || type.equalsIgnoreCase("Slideshow")){
					savePhotoDetailIntoDB(context, storyID, rssURL);
				}else if(type.equalsIgnoreCase("video")){
					if(rssURL.contains("article")){
						rssURL = "http://cloudapi.in.com/global/getJsonData.php?app=FIRST_POST&url=http%3A%2F%2Fapi.firstpost.com%2Fipad2%2FnewAPI%2Fvideosingle.php%3Fformat%3Djson%26postid%3D" +storyID;
					}
					saveVideoDetailIntoDB(context,storyID,rssURL,type);
				}else if(type.equalsIgnoreCase("audio")){
					saveAudioDetailIntoDB(context,storyID,rssURL,type);
				}else{
					saveNewsDetailIntoDB(context,storyID,rssURL,"news");
				}	
			}else{
				makeFav(context, storyID, rssURL, type, headline);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	*/
	}
	
	private void makeFav(Context context,String storyID, String rssURL,String type,String headline){
	/*	String detailJson = "";
		try{
			JSONObject mJsonObject = new JSONObject();
			try{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ID",storyID);
				jsonObject.put("post_title",headline);
				jsonObject.put("content_type",type);
				jsonObject.put("post_content_type",type);
				
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(jsonObject);
				
				mJsonObject.put("data",jsonArray);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			detailJson = mJsonObject.toString();
			FirstpostDatabaseManager firstpostDatabaseManager = new FirstpostDatabaseManager(context);
			firstpostDatabaseManager.insertIntoDetail(type, storyID, detailJson, "", rssURL, type,"","","");
			firstpostDatabaseManager.makeNewsFavorite(storyID, "true");	
		}catch(Exception e){
			e.printStackTrace();
		}
	*/
	}
	
	
	private void saveNewsDetailIntoDB(final Context context,final String storyID,final String rssURL,final String type){
	/*	final FirstpostDatabaseManager firstpostDatabaseManager = new FirstpostDatabaseManager(context);
		new Thread() {
			@Override
			public void run() {
				try {
					articleDetail = Parse.getInstance().getNewsDetailDataFromDb(storyID, context, rssURL);
					if(articleDetail==null){
						articleDetail = Parse.getInstance().getNewsDetailData("",storyID,context,rssURL,"","");
					}
					///Make news favourite
					if(articleDetail!=null){
						firstpostDatabaseManager.makeNewsFavorite(storyID,"true");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			};
		}.start();
	*/}
	
	private void savePhotoDetailIntoDB(final Context context,final String storyID,final String rssURL){
	/*	final FirstpostDatabaseManager firstpostDatabaseManager = new FirstpostDatabaseManager(context);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				photoDetail = Parse.getInstance().getPhotoDetailData("",context,rssURL,"");
				if(photoDetail!=null){
					firstpostDatabaseManager.makeNewsFavorite(storyID,"true");
				}
			}
		}).start();
	*/}

	private void saveAudioDetailIntoDB(final Context context, String storyID,final String rssURL, String type) {
	/*	final FirstpostDatabaseManager firstpostDatabaseManager = new FirstpostDatabaseManager(context);
		new Thread() {
			@Override
			public void run() {
				try {
					articleDetail = Parse.getInstance().getAudioDetailData(context,rssURL);
					if(articleDetail!=null){
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();
	*/}

	private void saveVideoDetailIntoDB(final Context context, final String storyID,final String rssURL, String type) {
		/*final FirstpostDatabaseManager firstpostDatabaseManager = new FirstpostDatabaseManager(context);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					articleDetail = Parse.getInstance().getVideoDetailData("Video", context,rssURL,"");
					if(articleDetail!=null){
						firstpostDatabaseManager.makeNewsFavorite(storyID, "true");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		*/
	}
	
}
