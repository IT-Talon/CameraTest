package com.talon.camerademo;

import android.text.TextUtils;

import java.io.FileInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FileType
{
    private static final Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    private static final Set<Integer> TYPE_LENGTH_SET = new TreeSet<>(new Comparator<Integer>()
    {
        public int compare(Integer value1, Integer value2)
        {
            return value2 - value1;
        }
    });

    private static int maxTypeLength = 0;

    static
    {
        //Images
        FILE_TYPE_MAP.put("FFD8FF", "jpg");
        FILE_TYPE_MAP.put("89504E47", "png");
        FILE_TYPE_MAP.put("47494638", "gif");
        FILE_TYPE_MAP.put("49492A00", "tif");
        FILE_TYPE_MAP.put("424D", "bmp");
        FILE_TYPE_MAP.put("38425053", "psd");

        //Multimedia
        FILE_TYPE_MAP.put("57415645", "wav");
        FILE_TYPE_MAP.put("41564920", "avi");
        FILE_TYPE_MAP.put("2E524D46", "rm");
        FILE_TYPE_MAP.put("000001BA", "mpg");
        FILE_TYPE_MAP.put("000001B3", "mpg");
        FILE_TYPE_MAP.put("6D6F6F76", "mov");
        FILE_TYPE_MAP.put("4D546864", "mid");

        //Others
        FILE_TYPE_MAP.put("3C3F786D6C", "xml");
        FILE_TYPE_MAP.put("68746D6C3E", "html");
        FILE_TYPE_MAP.put("D0CF11E0", "doc");
        FILE_TYPE_MAP.put("504B0304", "zip");
        FILE_TYPE_MAP.put("52617221", "rar");
        FILE_TYPE_MAP.put("1F8B08", "gz");

        int typeLength;

        for(String type: FILE_TYPE_MAP.keySet())
        {
            typeLength = type.length();
            maxTypeLength = Math.max(typeLength, maxTypeLength);
            TYPE_LENGTH_SET.add(typeLength);
        }
    }

    /**
     * 获取文件类型
     * @param path 文件路径
     * @return 文件类型，具体值与对应的扩展名一致，全小写
     */
    public static String getFileType(String path)
    {
        String type = null;
        String header = getFileHeader(path);

        if(header != null)
        {
            header = header.toUpperCase();
            int headerLength = header.length();
            String partlyHeader;

            for(int length: TYPE_LENGTH_SET)
            {
                if(headerLength >= length)
                {
                    partlyHeader = header.substring(0, length);
                    type = FILE_TYPE_MAP.get(partlyHeader);

                    if(type != null)
                    {
                        break;
                    }
                }
            }
        }

        return type;
    }

    /**
     * 判断指定文件是不是指定的类型
     * @param path 文件路径
     * @param typeName 文件类型，具体值与对应的扩展名一致
     * @return
     */
    public static boolean isTypeOf(String path, String typeName)
    {
        String fileType = getFileType(path);
        return !TextUtils.isEmpty(fileType) && fileType.equalsIgnoreCase(typeName);
    }

    private static String getFileHeader(String path)
    {
        FileInputStream inputStream = null;
        String header = null;

        try
        {
            inputStream = new FileInputStream(path);
            byte[] bytes = new byte[maxTypeLength];
            inputStream.read(bytes, 0, bytes.length);
            header = bytesToHex(bytes);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                    inputStream = null;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return header;
    }

    private static String bytesToHex(byte bytes[])
    {
        return bytesToHex(bytes, 0, bytes.length, null);
    }

    private static String bytesToHex(byte bytes[], int start, int end, String split)
    {
        StringBuilder sb = new StringBuilder();
        boolean hasSplit = split != null && !split.isEmpty();
        end = Math.min(end, bytes.length);

        for(int i = start; i < end; i++)
        {
            if(hasSplit && i > start)
            {
                sb.append(split);
            }

            sb.append(byteToHex(bytes[i]));
        }

        return sb.toString();
    }

    private static String byteToHex(byte b)
    {
        String hex = Integer.toHexString(0xFF & b | 0x00);
        return b >= 0 && b <= 15? '0' + hex: hex;
    }
}