package com.example.editanywhere.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson2.JSON;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.example.editanywhere.entity.model.Entry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FileUtils {

    private static final String TAG = "FileUtils";
    private static final String MATCH_ALL = "*";


    private static final Set<String> WHITE_SET = new HashSet<>();
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

    static {
        WHITE_SET.add(MATCH_ALL);
    }

    public static List<String[]> readCSVFile(String filePath, char delimiter) {
        if (filePath == null || filePath.length() == 0 || !filePath.endsWith("csv")) {
            Log.w(TAG, "readCSVFile: invalid csv file name");
            return new ArrayList<>();
        }
        return readCSV(filePath, delimiter);
    }

    public static boolean writeCSVFile(List<String[]> list, String filePath, char delimiter) {
        if (filePath == null || filePath.length() == 0 || !filePath.endsWith("csv")) {
            Log.w(TAG, "writeCSVFile: invalid csv file name");
            return false;
        }
        if (list == null || list.size() == 0) {
            Log.w(TAG, "writeCSVFile: list is empty, nothing to be written");
            return false;
        }
        return writeCSV(list, filePath, delimiter);
    }

    public static <T> List<T> readJsonFile(String filePath, Class<T> clazz) {
        if (filePath == null || filePath.length() == 0 || !filePath.endsWith("json")) {
            Log.w(TAG, "readJsonFile: invalid json file name");
            return new ArrayList<>();
        }
        File file = getFile(filePath);
        if (file == null) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis, Charset.defaultCharset()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return JSON.parseArray(sb.toString(), clazz);
        } catch (Exception e) {
            Log.e(TAG, "readJsonFile: error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static boolean writeJsonFile(List<?> list, String filePath) {
        if (filePath == null || filePath.length() == 0 || !filePath.endsWith("json")) {
            Log.w(TAG, "writeJsonFile: invalid json file name");
            return false;
        }
        File file = getFile(filePath);
        if (file == null) {
            return false;
        }
        try (Writer writer = new PrintWriter(file)) {
            String str = JSON.toJSONString(list);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(str);
            bufferedWriter.flush();
            bufferedWriter.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeJsonFile: error: " + e.getMessage());
        }
        return false;
    }

    private static File getFile(String filePath) {
        boolean hasAccess = false;
        for (String prefix : WHITE_SET) {
            if (MATCH_ALL.equals(prefix)) {
                hasAccess = true;
                break;
            }
            if (filePath.startsWith(prefix)) {
                hasAccess = true;
                break;
            }
        }
        if (!hasAccess) {
            return null;
        }
        try {
            return new File(filePath);
        } catch (Exception e) {
            Log.e(TAG, "getFile: " + e.getMessage());
        }
        return null;
    }

    private static List<String[]> readCSV(String filePath, char delimiter) {
        File file = getFile(filePath);
        if (file == null) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            CsvReader csvReader = new CsvReader(
                    new InputStreamReader(fis, Charset.defaultCharset()));
            csvReader.setDelimiter(delimiter);
            List<String[]> res = new ArrayList<>();
            while (csvReader.readRecord()) {
                res.add(csvReader.getValues());
            }
            return res;
        } catch (Exception e) {
            Log.e(TAG, "readCSV: error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private static boolean writeCSV(List<String[]> list, String filePath, char delimiter) {
        File file = getFile(filePath);
        if (file == null) {
            Log.w(TAG, "writeCSV: no access to write csv file");
            return false;
        }
        try (Writer writer = new PrintWriter(file)) {
            CsvWriter csvWriter = new CsvWriter(writer, delimiter);
            for (String[] line : list) {
                csvWriter.writeRecord(line);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeCSV: error: " + e.getMessage());
            return false;
        }
    }

    public static Uri toUri(Context context, String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationInfo().packageName + ".fileprovider", new File(filePath));
        }
        return Uri.fromFile(new File(filePath));
    }

    /**
     * Android 10 以上适配 另一种写法
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    public static String getFileFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath;
        String[] filePathColumn = {MediaStore.DownloadColumns.DATA, MediaStore.DownloadColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return filePath;
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{"_data"};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow("_data");
                String var8 = cursor.getString(column_index);
                return var8;
            }
        } catch (IllegalArgumentException var12) {
            Log.i("FileUtils", String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", var12.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return null;
    }

    @SuppressLint({"NewApi"})
    public static String getPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            String id;
            String[] split;
            String type;
            if (isExternalStorageDocument(uri)) {
                id = DocumentsContract.getDocumentId(uri);
                split = id.split(":");
                type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    try {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, (String) null, (String[]) null);
                    } catch (NumberFormatException var9) {
                        Log.i("FileUtils", var9.getMessage());
                        return null;
                    }
                }
            } else if (isMediaDocument(uri)) {
                id = DocumentsContract.getDocumentId(uri);
                split = id.split(":");
                type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, "_id=?", selectionArgs);
            }
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                return getDataColumn(context, uri, (String) null, (String[]) null);
            }

            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static boolean copyFile(FileInputStream fileInputStream, String outFilePath) throws IOException {
        if (fileInputStream == null) {
            return false;
        } else {
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;

            boolean var5;
            try {
                inputChannel = fileInputStream.getChannel();
                outputChannel = (new FileOutputStream(new File(outFilePath))).getChannel();
                inputChannel.transferTo(0L, inputChannel.size(), outputChannel);
                inputChannel.close();
                boolean var4 = true;
                return var4;
            } catch (Exception var9) {
                var5 = false;
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }

                if (inputChannel != null) {
                    inputChannel.close();
                }

                if (outputChannel != null) {
                    outputChannel.close();
                }

            }

            return var5;
        }
    }

    public static void copyFile(@NonNull String pathFrom, @NonNull String pathTo) throws IOException {
        if (!pathFrom.equalsIgnoreCase(pathTo)) {
            FileChannel outputChannel = null;
            FileChannel inputChannel = null;

            try {
                inputChannel = (new FileInputStream(new File(pathFrom))).getChannel();
                outputChannel = (new FileOutputStream(new File(pathTo))).getChannel();
                inputChannel.transferTo(0L, inputChannel.size(), outputChannel);
                inputChannel.close();
            } finally {
                if (inputChannel != null) {
                    inputChannel.close();
                }

                if (outputChannel != null) {
                    outputChannel.close();
                }

            }

        }
    }

    public static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + sf.format(millis);
    }

    public static String getCreateFileName() {
        long millis = System.currentTimeMillis();
        return sf.format(millis);
    }

    public static String rename(String fileName) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return temp + "_" + getCreateFileName() + suffix;
    }
}
