import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * Created by Malek on 5/27/2017.
 */

public class HttpRequest {
    private static StringResponse stringResponse;
    private static ImageResponse imageResponse;

    interface StringResponse {
        void OnComplete(String response);
    }
    interface ImageResponse {
        void OnComplete(Bitmap response);
    }


    public static void GET(String URL,HashMap<String,String> Parameters,  StringResponse request){
        stringResponse = request;

        String data = "";
        try {
            for ( String key : Parameters.keySet() ) {

                data += key + "=" + URLEncoder.encode(Parameters.get(key), "UTF-8") + "&";
            }
            new DownloadWebpageTask().execute(URL+"?"+Parameters,"GET","");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static void POST(String URL,HashMap<String,String> Parameters,  StringResponse request){
        stringResponse = request;

        String data = "";
        try {
            for ( String key : Parameters.keySet() ) {

                data += key + "=" + URLEncoder.encode(Parameters.get(key), "UTF-8") + "&";
            }
            new DownloadWebpageTask().execute(URL+"?"+Parameters,"POST","").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static void Upload_File(String URL,String Name , String Path,  StringResponse request){
        stringResponse = request;
        new UploadFilesTask().execute(URL,Name,Path);
    }
    public static void Download_Image(String URL,  ImageResponse request){
        imageResponse = request;
        new DownloadImageTask().execute(URL);
    }

    private static class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

// params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0], urls[1], urls[2]);
            } catch (IOException e) {
                stringResponse.OnComplete("Unable to retrieve web page. URL may be invalid.");
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            stringResponse.OnComplete(result);
        }


        private String downloadUrl(String myurl, String method, String data) throws IOException {
            InputStream is = null;
// Only display the first 500 characters of the retrieved
// web page content.


            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod(method);
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Connection", "close");
                if (method == "POST") {
                    conn.setDoOutput(true);

                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    PrintWriter out = new PrintWriter(conn.getOutputStream());
                    out.print(data);
                    out.close();
                }
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string


                String contentAsString = readIt(is, conn.getContentLength());
                return contentAsString;


                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }


        private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
            return writer.toString();
        }



    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... url) {
            try {
                return downloadUrl(url[0]);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageResponse.OnComplete(result);
        }


        private static Bitmap downloadUrl(String strUrl) throws IOException {
            Bitmap bitmap = null;
            InputStream iStream = null;
            try {
                URL url = new URL(strUrl);
                /** Creating an http connection to communcate with url */
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                /** Connecting to url */
                urlConnection.connect();

                /** Reading data from url */
                iStream = urlConnection.getInputStream();

                /** Creating a bitmap from the stream returned from the url */
                bitmap = BitmapFactory.decodeStream(iStream);

            } catch (Exception e) {

            } finally {
                iStream.close();
            }
            return bitmap;
        }

    }


    private static class UploadFilesTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                return  downloadUrl(urls[0],urls[1],urls[2]);
            } catch (IOException e) {

            }
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            stringResponse.OnComplete(result);
        }

        private String downloadUrl(String myurl,String parameter , String path) throws IOException {
            InputStream is = null;
            String boundary ;
            String LINE_FEED = "\r\n";
            try {
                boundary = "---------------------------" + System.currentTimeMillis();
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setRequestProperty("Connection", "close");

                File uploadFile = new File(path);
                if(uploadFile.exists()) {

                    conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
                    conn.setDoOutput(true);


                    OutputStream outputStream = conn.getOutputStream();
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),true);

                    String fileName = uploadFile.getName();

                    out.append("--" + boundary).append(LINE_FEED);
                    out.append(
                            "Content-Disposition: form-data; name=\"" + parameter
                                    + "\"; filename=\"" + fileName + "\"")
                            .append(LINE_FEED);
                    out.append(
                            "Content-Type: "
                                    + URLConnection.guessContentTypeFromName(fileName))
                            .append(LINE_FEED);
                    out.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    out.append(LINE_FEED);
                    out.flush();

                    FileInputStream inputStream = new FileInputStream(uploadFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    inputStream.close();

                    out.append(LINE_FEED);
                    out.flush();
                    out.append(LINE_FEED).flush();
                    out.append("--" + boundary + "--").append(LINE_FEED);
                    out.close();
                }
                conn.connect();

                int response = conn.getResponseCode();

                is = conn.getInputStream();

                // Convert the InputStream into a string


                String contentAsString = readIt(is, conn.getContentLength());
                return contentAsString;


                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }

        }

        private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
            return writer.toString();
        }


    }

}
