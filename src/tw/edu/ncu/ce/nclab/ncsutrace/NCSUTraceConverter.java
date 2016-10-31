package tw.edu.ncu.ce.nclab.ncsutrace;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

import tw.edu.ncu.ce.nclab.ncutrace.TraceArrangement;


public class NCSUTraceConverter extends TraceArrangement {

	private String NCSUTracePrefix;

	protected String[] getNCSUFileName(File directory) {
		FilenameFilter trackFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {

				if (name.matches(NCSUTracePrefix + "_30sec_\\d+\\.txt")) {
					return true;
				} else {
					return false;
				}
			}
		};

		return directory.list(trackFilter);
	}

	public String getNCSUTracePrevix() {
		return NCSUTracePrefix;
	}

	public void startConverter() throws IOException {

		checkSourceDirectory("_cvt");
		NCSUTracePrefix = sourceDirectory.getName();

		String[] files = getNCSUFileName(sourceDirectory);

		int i = 0;
		for (String fileName : files) {
			System.out.println(fileName);
			Scanner sc = new Scanner(new File(sourceDirectory.getAbsolutePath()
					+ File.separator + fileName));

			PrintWriter pw = new PrintWriter(new File(
					outPutDirectory.getAbsolutePath() + File.separator + i
							+ ".txt"));
			i++;
			while (sc.hasNextLine()) {
				String trackInfo = sc.nextLine();
				// 0.0000000000000000e+000 -2.3826021716531892e+002  1.0661691368875292e+003
				String tokens[] = trackInfo.split("\\s+");
				// 每句前面好像有兩個空白，所以值的index從1開始算
				BigDecimal time = new BigDecimal(tokens[1])
						.stripTrailingZeros();
				BigDecimal x = new BigDecimal(tokens[2]).setScale(1,
						RoundingMode.HALF_UP);
				BigDecimal y = new BigDecimal(tokens[3]).setScale(1,
						RoundingMode.HALF_UP);

				pw.println(x.toPlainString() + " " + y.toPlainString() + " "
						+ time.toPlainString());
				pw.flush();

			}
			pw.close();
			sc.close();

		}

	}

}
