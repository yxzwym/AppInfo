package cm.cym.packinfo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;

/**
 * 签名工具类
 */
public class SignatureUtil {
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    /**
     * byte转16进制字符串
     *
     * @param b 字节
     * @return 16进制字符串
     */
    private static String byteToHex(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 转换字节数组为16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i < bytes.length - 1) {
                str.append(byteToHex(bytes[i])).append(":");
            } else {
                str.append(byteToHex(bytes[i]));
            }
        }
        return str.toString();
    }

    /**
     * 根据包名，获取MD5
     *
     * @param context  context
     * @param packName 包名
     * @return md5
     */
    public static String getMd5(Context context, String packName) {
        Signature[] sigs = null;
        StringBuilder signature = new StringBuilder();
        try {
            sigs = context.getPackageManager().getPackageInfo(packName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature sig : sigs) {
                MessageDigest md;
                // MD5
                md = MessageDigest.getInstance("MD5");
                md.update(sig.toByteArray());
                signature.append(byteArrayToHex(md.digest()));
            }
        } catch (Exception ignored) {
        }
        return signature.toString();
    }

    /**
     * 根据包名，获取Sha1
     *
     * @param context  context
     * @param packName 包名
     * @return md5
     */
    public static String getSha1(Context context, String packName) {
        Signature[] sigs = null;
        StringBuilder signature = new StringBuilder();
        try {
            sigs = context.getPackageManager().getPackageInfo(packName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature sig : sigs) {
                MessageDigest md;
                // SHA1
                md = MessageDigest.getInstance("SHA");
                md.update(sig.toByteArray());
                signature.append(byteArrayToHex(md.digest()));
            }
        } catch (Exception ignored) {
        }
        return signature.toString();
    }

    /**
     * 根据包名，获取Sha1 Base64
     *
     * @param context  context
     * @param packName 包名
     * @return md5
     */
    public static String getSha1Base64(Context context, String packName) {
        Signature[] sigs = null;
        StringBuilder signature = new StringBuilder();
        try {
            sigs = context.getPackageManager().getPackageInfo(packName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature sig : sigs) {
                MessageDigest md;
                // SHA1 Base64
                md = MessageDigest.getInstance("SHA");
                md.update(sig.toByteArray());
                signature.append(new String(Base64.encode(md.digest(), 0)));
            }
        } catch (Exception ignored) {
        }
        return signature.toString();
    }
}
