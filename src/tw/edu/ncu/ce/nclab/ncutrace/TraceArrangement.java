package tw.edu.ncu.ce.nclab.ncutrace;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;

public abstract class TraceArrangement {

	protected File sourceDirectory;
	protected File outPutDirectory;

	protected void chooseSourceDirectory() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setVisible(true);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			sourceDirectory = fc.getSelectedFile();

		} else {
			System.exit(0);
		}
	}

	
	protected void checkSourceDirectory(String outputPostfix) {
		if (sourceDirectory == null) {
			chooseSourceDirectory();
		}
		outPutDirectory = new File(sourceDirectory.getAbsolutePath()
				+ outputPostfix);
		outPutDirectory.mkdirs();
	}
	
	/**
	 * 檢查資料來源的目錄，若未設定，就會跳出FileChooser
	 */
	protected void checkSourceDirectory() {
		if (sourceDirectory == null) {
			chooseSourceDirectory();
		}
		outPutDirectory = new File(sourceDirectory.getAbsolutePath());
		outPutDirectory.mkdirs();
	}

	protected File getOutPutDirectory() {
		return this.outPutDirectory;
	}

	/**
	 * 
	 * @param directory
	 * @return
	 */
	protected String[] getNumericFileName(File directory) {
		FilenameFilter trackFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.matches("\\d+\\.txt")) {
					return true;
				} else {
					return false;
				}
			}
		};

		return directory.list(trackFilter);
	}

}
