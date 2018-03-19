package edu.unc.ceccr.chembench.workflows.descriptors;

import com.google.common.base.CharMatcher;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DescriptorIsida implements DescriptorSet {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorIsida.class);
    private static final Pattern ISIDA_HEADER_REGEX = Pattern.compile("\\s*\\d+\\.\\s*(.+)");
    private static final Pattern ISIDA_FILENAME_REGEX = Pattern.compile("(.*\\.ISIDA)(\\.svm(_\\d+)?)?");

    @Override
    public String getDescriptorSetName() { return Constants.ISIDA;}

    @Override
    public String getFileEnding() { return ".ISIDA";}

    public String getFileHdrEnding() { return ".ISIDA.hdr";}

    public String getFileSvmEnding() { return ".ISIDA.svm";}

    public String getFileRenormEnding() { return ".renorm.ISIDA";}

    @Override
    public String getFileErrorOut() { return "ISIDA.out";}

    @Override
    public void generateDescriptors(String sdfile, String outfile) {
        logger.debug("isida generateDescriptors");
        //Given an SDF file, run ISIDA to get the chemical descriptors for each compound
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        Path workingDir = Paths.get(sdfile.replaceAll("/[^/]+$", "")).resolve("Descriptors");
        Path inFilePath = workingDir.relativize(Paths.get(sdfile));
        Path outFilePath = workingDir.relativize(Paths.get(outfile + getFileEnding()));
        String execstr = String.format("Fragmentor -i %s -o %s -t 0 -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name",
                inFilePath.toString(), outFilePath.toString());
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir.toString(), "ISIDA");
    }

    public void generateIsidaDescriptorsWithHeader(String sdfile, String outfile, String headerFile) {
        logger.debug("isida generateDescriptorsWithHeader");
        //Given an SDF file, run ISIDA to get the chemical descriptors for each compound with the .hdr from predictor
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        Path workingDir = Paths.get(sdfile.replaceAll("/[^/]+$", "")).resolve("Descriptors");
        Path inFilePath = workingDir.relativize(Paths.get(sdfile));
        Path outFilePath = workingDir.relativize(Paths.get(outfile));
        Path headerFilePath = workingDir.relativize(Paths.get(headerFile));
        String execstr = String.format("Fragmentor -i %s -o %s -t 0 -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name "
                + "-h %s --StrictFrg", inFilePath.toString(), outFilePath.toString(), headerFilePath.toString());
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir.toString(), "ISIDA");
    }

    @Override
    public void readDescriptors(String ISIDAOutputFile, List<String> descriptorNames,
                                            List<Descriptors> descriptorValueMatrix) throws Exception {
        logger.debug("isida readDescriptors");
        ISIDAOutputFile += getFileEnding();
        readDescriptorFile (ISIDAOutputFile, descriptorNames, descriptorValueMatrix);
    }

    @Override
    public void readDescriptorsChunks(String outputFile, List<String> descriptorNames,
                                      List<Descriptors> descriptorValueMatrix) throws Exception {
        logger.debug("isida readDescriptorChunks");
        readDescriptorFile (outputFile, descriptorNames, descriptorValueMatrix);
    }

    @Override
    public String splitFile(String workingDir, String descriptorsFile) throws Exception {
        logger.debug("isida splitFile");

        descriptorsFile += ".renorm.ISIDA.svm";

        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read ISIDA descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);
        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart =
                new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));

        // // Added to copy CDK, testing if this makes ISIDA work
        // String header = br.readLine() + "\n";
        // String descriptorNames = br.readLine() + "\n";
        // outFilePart.write(header);
        // outFilePart.write(descriptorNames);
        // // change ended

        String line;
        while ((line = br.readLine()) != null) {
            outFilePart.write(line + "\n");

            moleculesInCurrentFile++;
            if (moleculesInCurrentFile == compoundsPerChunk) {
                outFilePart.close();
                moleculesInCurrentFile = 0;
                currentFile++;
                outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
                // // new testing line
                // outFilePart.write(header);
                // outFilePart.write(descriptorNames);
                // // test ended
            }
        }
        br.close();
        outFilePart.newLine();
        outFilePart.close();

        return descriptorsFile;
    }

    @Override
    public String checkDescriptors(String outputFile) throws Exception {
        logger.debug("isida checkDescriptors");
        File hdrFile = new File(outputFile + getFileHdrEnding());
        File svmFile = new File (outputFile + getFileSvmEnding());
        String errors = "";

        if (!(hdrFile.exists() && svmFile.exists())) {
            errors = "Cannot find ISIDA files";
        }
        return errors;
    }

    private void readDescriptorFile (String outputFile, List<String> descriptorNames, List<Descriptors>
            descriptorValueMatrix) throws Exception{
        // logger.debug("isida readDesciptorFile_begin");
        logger.debug("reading ISIDA Descriptors");
        Path rawFilePath = Paths.get(outputFile);
        Path dirPath = rawFilePath.getParent();
        // for filenames like "modeling.sdf.ISIDA.svm" or even "modeling.sdf.ISIDA.svm_0" (split files),
        // remove the ".svm..." extension (assumption is that the header file is "modeling.sdf.ISIDA.hdr")
        Matcher m = ISIDA_FILENAME_REGEX.matcher(rawFilePath.getFileName().toString());
        m.matches();
        Path headerFilePath = dirPath.resolve(m.group(1) + ".hdr");
        Path datafilePath;
        if (m.group(2) == null) {
            datafilePath = dirPath.resolve(m.group(1) + ".svm");
        } else {
            datafilePath = dirPath.resolve(m.group(1) + m.group(2));
        }

        List<String> fragments = new ArrayList<>();
        fragments.add(""); // XXX fence-value for [0] since fragments are 1-indexed
        try (BufferedReader reader = Files.newBufferedReader(headerFilePath, StandardCharsets.UTF_8)) {
            String line;
            // isida header (.hdr) file structure: fragment number -> descriptor name,
            // where numbering starts from 1
            //    1.         Cl
            // ... (snip)
            //   24.         H-C
            //   25.         H-C*C-H
            //   26.         (Cl-C),(Cl-C*C),(Cl-C*C),(Cl-C*C*C),(Cl-C*C*C),(Cl-C*C-H),(Cl-C*C-N),xCl
            // ...
            while ((line = reader.readLine()) != null) {
                Matcher matcher = ISIDA_HEADER_REGEX.matcher(line);
                matcher.matches();
                fragments.add(matcher.group(1));
            }
        } catch (IOException e) {
            logger.debug("isida error 0");
            throw new RuntimeException("Couldn't read ISIDA header file", e);
        } catch (IndexOutOfBoundsException e1){
            logger.debug("isida error 1");
            throw new IndexOutOfBoundsException("array section 1 error");
        }
        logger.debug("isida readDesciptorFile_processing");
        // XXX LinkedHashMap is important: need to keep this map's keys in order of insertion
        LinkedHashMap<String, SortedMap<Integer, Integer>> compoundNameToFragmentCounts = Maps.newLinkedHashMap();
        try (BufferedReader reader = Files.newBufferedReader(datafilePath, StandardCharsets.UTF_8)) {
            // isida data (.svm) file structure: compound name, followed by <fragment number>:<fragment count> pairs,
            // where pairs are ordered by ascending fragment number
            // ... (snip)
            // 6 1:1 2:4 3:2 4:6 5:3 6:1 7:2 8:2 9:1 10:1 11:2 12:4 13:4 ...
            // 289 2:2 4:6 5:6 19:6 20:6 21:6 22:8 23:8 24:4 25:3 39:1 ...
            // 370 2:5 4:7 5:6 19:6 20:6 21:6 22:4 23:4 24:2 39:2 40:4 ...
            // ...
            String line;
            Splitter lineSplitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();
            Splitter pairSplitter = Splitter.on(':');
            while ((line = reader.readLine()) != null) {
                List<String> items = lineSplitter.splitToList(line);
                String compoundName = items.get(0);
                SortedMap<Integer, Integer> fragmentCounts = Maps.newTreeMap();
                for (String fragmentPair : items.subList(1, items.size())) {
                    List<String> values = pairSplitter.splitToList(fragmentPair);
                    fragmentCounts.put(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)));
                }
                compoundNameToFragmentCounts.put(compoundName, fragmentCounts);
            }
        } catch (IOException e) {
            logger.debug("isida error 3" );
            throw new RuntimeException("Couldn't read ISIDA data file", e);
        } catch (IndexOutOfBoundsException e1){
            logger.debug("isida error 2");
            throw new IndexOutOfBoundsException("array section2 error");
        }
        logger.debug("isida readDesciptorFile_processing2");
        int compoundIndex = 1; // Descriptors.compoundIndex is 1-indexed
        if (descriptorValueMatrix == null) {
            descriptorValueMatrix = new ArrayList<>();
        }
        // XXX fragment names are 1-indexed in the .hdr file
        try {
            descriptorNames.addAll(fragments.subList(1, fragments.size()));
            for (String compoundName : compoundNameToFragmentCounts.keySet()) {
                SortedMap<Integer, Integer> fragmentCounts = compoundNameToFragmentCounts.get(compoundName);
                Descriptors d = new Descriptors();
                d.setCompoundIndex(compoundIndex++);
                d.setCompoundName(compoundName);
                List<Double> fragmentCountsForCompound = new ArrayList<>();
            // XXX fragments are 1-indexed (note loop starting point)
                for (int i = 1; i < fragments.size(); i++) {
                    fragmentCountsForCompound.add(MoreObjects.firstNonNull(fragmentCounts.get(i), 0).doubleValue());
                }
                d.setDescriptorValues(fragmentCountsForCompound);
                descriptorValueMatrix.add(d);
            }
        } catch (IndexOutOfBoundsException e1){
            logger.debug("isida error 4");
            throw new IndexOutOfBoundsException("array section3 error");
        }
        logger.debug("isida readDesciptorFile_ending");

    }

}