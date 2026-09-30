package net.scapeemulator.asm.bundler;

import net.scapeemulator.asm.bundler.trans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public final class ClientBundler {

	private static final Logger logger = LoggerFactory.getLogger(ClientBundler.class);
	// private static final String JAVA_PATH = "\"C:\\Program
	// Files\\Java\\jdk1.7.0_67\\bin\\";

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		ClientBundler bundler = new ClientBundler();
		bundler.bundle();
	}

	public void bundle() throws IOException, NoSuchAlgorithmException {
		/* unpack source files */

		logger.info("Starting client bundler...");
		Application client = Application.unpackJar(new File("data/runescape.jar"));
		// Application glClient = Application.unpack200(new
		// File("data/runescape_gl.pack200"));
		// TODO: loader_gl
		Application loader = Application.unpackJar(new File("data/loader.jar"));
		// Application glLoader = Application.unpackJar(new
		// File("data/loader_gl.jar"));

		/* update client RSA keys */
		logger.info("Transforming RSA keys...");
		Transformer transformer = new RsaTransformer();
		client.transform(transformer);
		// glClient.transform(transformer);

		/* remove hostname protection */
		logger.info("Transforming hostname protection...");
		transformer = new HostnameTransformer();
		client.transform(transformer);
		// glClient.transform(transformer);

		/* write everything but the loaders back out */
		logger.info("Bundling clients and libraries...");
		File clientOut = new File("../www/runescape.pack200");
		// File glClientOut = new File("../www/runescape_gl.pack200");
		// unsigned client output
		client.packJar(new File("../www/runescape.jar"));
		client.pack200(clientOut);
		// glClient.pack200(glClientOut);

		Resource unpacker = new Resource("game_unpacker.dat", "unpackclass.pack", false);
		Resource client_res = new Resource("../www/runescape.pack200");
		Resource runescape_js5 = Resource.SKIP;

		Resource jaggl_x86_win = new Resource("jaggl/jaggl.dll", "jaggl_0.lib", false);
		Resource jaggl_x64_win = Resource.SKIP;
		// new Resource("jaggl/jaggl64.dll", "jaggl_1.lib", true);

		Resource jaggl_x86_solaris = Resource.SKIP;// ??
		Resource jaggl_x64_solaris = Resource.SKIP;// ??

		Resource jni1 = Resource.SKIP;// ??
		Resource jni2 = Resource.SKIP;// ??
		Resource jni3 = Resource.SKIP;// ??
		Resource jni4 = Resource.SKIP;// ??

		Resource jmi_x86 = new Resource("/jagmisc/jagmisc.dll", "jagmisc_0.lib", false);
		Resource jmi_ms = Resource.SKIP;
		// new Resource("/jagmisc/jagmiscms.dll", "jagmisc_1.lib", true);
		Resource jmi_x64 = Resource.SKIP;
		// new Resource("/jagmisc/jagmisc64.dll", "jagmisc_2.lib", true);

		Resource sw3d = new Resource("/sw3d.dll", "sw3d_0.lib", false);

		Resource[] resources = new Resource[] { unpacker, client_res, runescape_js5, // Client
				jaggl_x86_win, jaggl_x64_win, // win jaggl
				jaggl_x86_solaris, jaggl_x64_solaris, // linux/solaris
				jni1, jni2, jni3, jni4, // IDK?
				jmi_x86, jmi_ms, jmi_x64, // JagMisc
				sw3d// sw3d
		};

		/* update SHA1 checksums in the loader */
		logger.info("Transforming SHA1 checksums and file sizes...");
		transformer = new ResourceTransformer(resources);
		loader.transform(transformer);

		/* transform cache path */
		logger.info("Transforming cache path...");
		transformer = new CachePathTransformer();
		loader.transform(transformer);
		// glLoader.transform(transformer);

		/* write the loaders back out */
		logger.info("Bundling loaders...");
		loader.packJar(new File("../www/loader.jar"));
		// glLoader.packJar(new File("../www/loader_gl.jar"));

		/* copy natives/unpacker */

		logger.info("Copying native libraries and game unpacker...");
		for (Resource resource : resources) {
			resource.build("../www/");
		}

		/* sign loaders */
		logger.info("Signing loaders...");
		Runtime.getRuntime().exec(
				"jarsigner -keystore data/scapeemu.keystore -storepass scapeemu ../www/loader.jar scapeemu");

		/* all done! */
		logger.info("Client bundler completed successfully.");
	}
}
