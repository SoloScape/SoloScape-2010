package net.scapeemulator.asm.bundler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resource {
	public static final Resource SKIP = new Resource();
	private static final Logger logger = LoggerFactory.getLogger(Resource.class);
	private final int unpackedLength;
	private final int packedLength;

	private final String source;
	private final String output;

	private final byte[] compressedData;
	private final int[] checksum;

	public Resource(String source, String output, boolean compress) throws IOException, NoSuchAlgorithmException {
		this.source = source;
		this.output = output;
		File file = new File("./data/" + source);
		if (!file.exists())
			throw new IOException("Resource: Tried to build an inexistent resource! " + source);
		unpackedLength = (int) file.length();
		checksum = sha1(file);

		if (compress) {
			byte[] data = readFile();
			Deflater deflater = new Deflater(Deflater.BEST_SPEED, true);
			byte[] temporary = new byte[unpackedLength];
			deflater.setInput(data);
			packedLength = deflater.deflate(temporary, 0, unpackedLength);
			compressedData = new byte[packedLength];
			for (int i = 0; i < packedLength; i++) {
				compressedData[i] = temporary[i];
			}
			logger.info("Succesfully bundled Resource: " + source.split("/")[1] + " with compressed length: "
					+ packedLength);
		} else {
			packedLength = unpackedLength;
			compressedData = new byte[0];
		}
	}

	private Resource() {
		checksum = new int[20];
		compressedData = new byte[0];
		packedLength = unpackedLength = 0;
		output = "";
		source = "";
	}

	public Resource(String respath) throws NoSuchAlgorithmException, IOException {
		source = "";
		output = "";
		File file = new File(respath);
		if (!file.exists())
			throw new IOException("Resource: Tried to build an inexistent resource! " + respath);
		unpackedLength = (int) file.length();
		checksum = sha1(file);

		packedLength = unpackedLength;
		compressedData = new byte[0];
	}

	private static int[] sha1(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		try (InputStream is = new FileInputStream(file)) {
			byte[] buf = new byte[4096];
			for (;;) {
				int len = is.read(buf, 0, buf.length);
				if (len == -1) {
					byte[] bytes = digest.digest();

					int[] ints = new int[bytes.length];
					for (int i = 0; i < bytes.length; i++)
						ints[i] = bytes[i];
					return ints;
				}
				digest.update(buf, 0, len);
			}
		}
	}

	private byte[] readFile() throws IOException {
		byte[] data = new byte[unpackedLength];
		FileInputStream fis = new FileInputStream("./data/" + source);
		fis.read(data, 0, data.length);
		fis.close();
		return data;
	}

	public void build(String path) throws IOException {
		if (output.equalsIgnoreCase("")) {
			return;
		}
		if (compressedData.length != 0) {
			File file = new File(path, output);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(compressedData, 0, packedLength);
			fos.close();
		} else {
			File src = new File("./data/" + source);
			File dest = new File(path, output);
			Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public int getUnpackedLength() {
		return unpackedLength;
	}

	public int getPackedLength() {
		return packedLength;
	}

	public String getSource() {
		return source;
	}

	public byte[] getCompressedData() {
		return compressedData;
	}

	public int[] getChecksum() {
		return checksum;
	}

}
