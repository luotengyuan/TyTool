package com.lois.tytool.basej.secert.sm;

import com.lois.tytool.basej.secert.Base64Utils;
import com.lois.tytool.basej.io.StreamUtils;
import com.lois.tytool.basej.string.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sm4加密工具类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Sm4Security {
	
	private String secretKey = "";
	private String iv = "";
	private boolean hexString = false;
	private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	private static String ISO_8859_1 = "ISO-8859-1";
	private static String DEFAULT_ENCODING = "GBK";

	public static void main(String[] args) throws IOException {
		String plainText = "{\"Data\":{\"PersonName\":\"张三\",\"CardType\":\"0\",\"CardNo\":\"441322198278371023\"},\"OrgCode\":\"gjjkd1001_jsyh\"}";

		Sm4Security sm4Security = new Sm4Security();
		sm4Security.secretKey = "FFD74ADB821BD9E2";
		sm4Security.hexString = false;

		System.out.println("ECB模式");
		String cipherText = sm4Security.encryptDataToStringEcb(plainText);
		System.out.println("密文: " + cipherText);
		System.out.println("");

		plainText = sm4Security.decryptDataToStringEcb(cipherText);
		System.out.println("明文: " + plainText);
		System.out.println("");

		System.out.println("CBC模式");
		sm4Security.iv = "FFD74ADB821BD9E2";
		cipherText = sm4Security.encryptDataToStringCbc(plainText);
		System.out.println("密文: " + cipherText);
		System.out.println("");

		cipherText = "PIyE7r6xg5hDORAUtEwkGeErutm7R2vo4KjFla1LNI/O2WJK9OZP41noq6zQNPa67j2OgX7aEzm9/vUhZvkHFnh2xR9f/Qv19EKQWvZ3GWPcJzurXYHnvlCHtSxpf2U1biNeB7l/S3stpFKC3LmWuzXWBIh+k2y3Hil4FoGrAi+aaIHrDFx34by6Q7+Vk0SL";
		plainText = sm4Security.decryptDataToStringCbc(cipherText);
		System.out.println("明文: " + plainText);
	}
	
	public String decryptDataToStringCbc(String cipherText) {
		return decryptDataToStringCbc(cipherText, DEFAULT_ENCODING);
	}

	public String decryptDataToStringCbc(String cipherText, String charset) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_DECRYPT;

			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
				ivBytes = NumberUtils.hexStringToBytes(iv);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
				ivBytes = StringUtils.stringToBytes(iv);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyDec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4CryptCbc(ctx, ivBytes, Base64Utils.decode(cipherText, ISO_8859_1));
			return new String(decrypted, charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] decryptDataCbc(String cipherText) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_DECRYPT;

			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
				ivBytes = NumberUtils.hexStringToBytes(iv);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
				ivBytes = StringUtils.stringToBytes(iv);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyDec(ctx, keyBytes);
			return sm4.sm4CryptCbc(ctx, ivBytes, Base64Utils.decode(cipherText, ISO_8859_1));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decryptDataToStringCbc(byte[] bytes) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_DECRYPT;

			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
				ivBytes = NumberUtils.hexStringToBytes(iv);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
				ivBytes = StringUtils.stringToBytes(iv);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyDec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4CryptCbc(ctx, ivBytes, bytes);
			return new String(decrypted, DEFAULT_ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
	public byte[] decryptDataEcb(byte[] bytes) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_DECRYPT;

			byte[] keyBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyDec(ctx, keyBytes);
			return sm4.sm4CryptEcb(ctx, bytes);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] decryptDataEcb(String cipherText) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_DECRYPT;

			byte[] keyBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyDec(ctx, keyBytes);
			return sm4.sm4CryptEcb(ctx, Base64Utils.decode(cipherText, ISO_8859_1));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryptDataToStringEcb(String cipherText) {
		return decryptDataToStringEcb(cipherText, DEFAULT_ENCODING);
	}
	
	public String decryptDataToStringEcb(String cipherText, String charset) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_DECRYPT;

			byte[] keyBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyDec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4CryptEcb(ctx, Base64Utils.decode(cipherText, ISO_8859_1));
			return new String(decrypted, charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String encryptDataToStringCbc(byte[] bytes) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_ENCRYPT;

			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
				ivBytes = NumberUtils.hexStringToBytes(iv);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
				ivBytes = StringUtils.stringToBytes(iv);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyEnc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4CryptCbc(ctx, ivBytes, bytes);
			String cipherText = Base64Utils.encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String encryptDataToStringCbc(String plainText) {
		return encryptDataToStringCbc(plainText, DEFAULT_ENCODING);
	}
	public String encryptDataToStringCbc(String plainText, String charset) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_ENCRYPT;

			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
				ivBytes = NumberUtils.hexStringToBytes(iv);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
				ivBytes = StringUtils.stringToBytes(iv);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyEnc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4CryptCbc(ctx, ivBytes, plainText.getBytes(charset));
			String cipherText = Base64Utils.encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {

				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] encryptDataCbc(String plainText, String charset) {
		try {
			return encryptDataCbc(plainText.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}		
	}

	public byte[] encryptDataCbc(byte[] bytes) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_ENCRYPT;

			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
				ivBytes = NumberUtils.hexStringToBytes(iv);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
				ivBytes = StringUtils.stringToBytes(iv);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyEnc(ctx, keyBytes);
			return sm4.sm4CryptCbc(ctx, ivBytes, bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] encryptDataEcb(byte[] bytes) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_ENCRYPT;

			byte[] keyBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyEnc(ctx, keyBytes);
			return sm4.sm4CryptEcb(ctx, bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] encryptDataEcb(File f) throws IOException {
		if(f.exists()) {
			InputStream inStream = new FileInputStream(f);
			return encryptDataEcb(StreamUtils.readInputStreamToByteArray(inStream));
		} else {
			throw new IOException("File not Found");
		}
		
	}
	
	public byte[] encryptDataEcb(String plainText) {
		return encryptDataEcb(plainText, DEFAULT_ENCODING);
	}

	public byte[] encryptDataEcb(String plainText, String charset) {
		try {
			return encryptDataEcb(plainText.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String encryptDataToStringEcb(String plainText) {
			return encryptDataToStringEcb(plainText, DEFAULT_ENCODING);
	}


	public String encryptDataToStringEcb(String plainText, String charset) {
		try {
			Sm4ContextUtils ctx = new Sm4ContextUtils();
			ctx.isPadding = true;
			ctx.mode = Sm4BaseUtils.SM4_ENCRYPT;

			byte[] keyBytes;
			if (hexString) {
				keyBytes = NumberUtils.hexStringToBytes(secretKey);
			} else {
				keyBytes = StringUtils.stringToBytes(secretKey);
			}

			Sm4BaseUtils sm4 = new Sm4BaseUtils();
			sm4.sm4SetkeyEnc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4CryptEcb(ctx, plainText.getBytes(charset));
			String cipherText = Base64Utils.encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * @return the iv
	 */
	public String getIv() {
		return iv;
	}

	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @return the hexString
	 */
	public boolean isHexString() {
		return hexString;
	}

	/**
	 * @param hexString the hexString to set
	 */
	public void setHexString(boolean hexString) {
		this.hexString = hexString;
	}

	/**
	 * @param iv the iv to set
	 */
	public void setIv(String iv) {
		this.iv = iv;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	
}