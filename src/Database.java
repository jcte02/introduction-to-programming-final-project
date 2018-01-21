import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {
	//#region Singleton
	private static Database instance;

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					if (!instance.checkFileIntegrity()) {
						instance.restoreDatabaseFromMemory();
					}
				}
			});
		}
		return instance;
	}
	//#endregion

	//#region Instance
	private String md5Digest;
	private LinkedHashMap<String, Personality> memoryDatabase;
	private final static Path DATABASE_FILE_PATH = Paths.get("Resources/user-data.csv");

	private Database() {
		memoryDatabase = new LinkedHashMap<String, Personality>();
		slurpFileIntoMemory();
	}

	private void slurpFileIntoMemory() {
		md5Digest = getFileMD5Digest();
		try {
			for (String line : Files.readAllLines(DATABASE_FILE_PATH)) {
				Personality p = Personality.fromString(line);
				memoryTransaction(p);
			}
		} catch (IOException e) {
			// TODO: error popup
		}
	}
	//#endregion

	//#region Public
	public boolean addUser(Personality p) {
		if (memoryTransaction(p)) {
			addNewPersonality(p);
			return true;
		}
		return false;
	}

	public boolean isNicknameAvaiable(String nickname) {
		return !memoryDatabase.containsKey(nickname);
	}
	
	public void clear() {
		memoryDatabase.clear();
		restoreDatabaseFromMemory();
	}
	//#endregion

	//#region Transaction
	private boolean memoryTransaction(Personality p) {
		if (p != null && !memoryDatabase.containsKey(p.getNickname())) {
			memoryDatabase.put(p.getNickname(), p);
			return true;
		}
		return false;
	}

	private void addNewPersonality(Personality p) {
		if (!checkFileIntegrity()) {
			restoreDatabaseFromMemory();
		} else {
			String line = "\n" + p.toString() + "\n";
			try {
				Files.write(DATABASE_FILE_PATH, line.getBytes(), StandardOpenOption.APPEND);
				md5Digest = getMemoryMD5Digest();
			} catch (IOException e) {
				// TODO: error popup
			}
		}
	}
	//#endregion

	//#region Integrity
	private boolean checkFileIntegrity() {
		return Files.exists(DATABASE_FILE_PATH) && md5Digest.equals(getFileMD5Digest());
	}

	private void restoreDatabaseFromMemory() {
		try {
			Files.write(DATABASE_FILE_PATH, serializeMemoryAsString().getBytes());
			md5Digest = getMemoryMD5Digest();
		} catch (IOException e) {
			// TODO: error popup
		}
	}
	//#endregion

	//#region Digest
	private String getMemoryMD5Digest() {
		return getMD5Digest(serializeMemoryAsString().getBytes());
	}

	private String getFileMD5Digest() {
		byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(DATABASE_FILE_PATH);
		} catch (IOException e) {
			// file does not exist
			return null;
		}
		return getMD5Digest(bytes);
	}

	private String getMD5Digest(byte[] bytes) {
		MessageDigest message = null;
		try {
			message = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// MD5 is required to be implemented in every Java platform, will not throw
			return null;
		}
		message.update(bytes);
		return hexString(message.digest());
	}
	//#endregion

	//#region Byte operations
	private String serializeMemoryAsString() {
		StringBuilder sb = new StringBuilder();
		for (Personality p : memoryDatabase.values()) {
			sb.append(p.toString() + "\n");
		}
		return sb.toString();
	}

	private String hexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
	//#endregion

	public SearchResult searchUser(String nickname) {
		SearchResult result = new SearchResult();
		for (Map.Entry<String, Personality> e : memoryDatabase.entrySet()) {
			if (e.getKey().equals(nickname)) {
				result.addPerfectMatch(e.getValue());
			} else if (e.getKey().contains(nickname)) {
				result.addMatch(e.getValue());
			}
		}
		return result;
	}

	public PopulationStatistic getPopulationStatistic() {
		PopulationStatistic statistic = new PopulationStatistic();
		for (Personality p : memoryDatabase.values()) {
			statistic.addUser(p);
		}
		return statistic;
	}

}