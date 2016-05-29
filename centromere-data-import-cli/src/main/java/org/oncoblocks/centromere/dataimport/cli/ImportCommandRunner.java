/*
 * Copyright 2016 William Oemler, Blueprint Medicines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oncoblocks.centromere.dataimport.cli;

import com.google.common.collect.Iterables;
import org.oncoblocks.centromere.core.dataimport.*;
import org.oncoblocks.centromere.core.model.support.BasicDataFileMetadata;
import org.oncoblocks.centromere.core.model.support.DataFileMetadata;
import org.oncoblocks.centromere.core.model.support.DataSetMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handles execution of the {@code import} command arguments.  This command attempts to process
 *   data from a referenced input file and load it into the data warehouse.
 * 
 * @author woemler
 */
public class ImportCommandRunner {
	
	private final DataImportManager manager;
	
	private static final Logger logger = LoggerFactory.getLogger(ImportCommandRunner.class);

	public ImportCommandRunner(DataImportManager manager) {
		this.manager = manager;
	}

	/**
	 * Runs the import of the file provided in the input arguments.  Will choose the appropriate 
	 *   {@link RecordProcessor} instance, based on the supplied data type.  
	 * 
	 * @param arguments {@link ImportCommandArguments} instance, parsed from command line args.
	 * @throws Exception
	 */
	public void run(ImportCommandArguments arguments) throws Exception {
		logger.debug(String.format("[CENTROMERE] Starting ImportCommandRunner with arguments: %s", 
				 arguments.toString()));
		RecordProcessor processor = this.getProcessorByDataType(arguments.getDataType());
		logger.debug(String.format("[CENTROMERE] Using processor %s for data type %s.", 
				processor.getClass().getName(), arguments.getDataType()));
		BasicImportOptions options = arguments.getImportOptions();
		logger.debug(String.format("[CENTROMERE] Running import with options: %s", options.toString()));
		DataSetMetadata dataSetMetadata = null;
		DataFileMetadata dataFileMetadata = null;
		String input = arguments.getInputFilePath();
		Map<String, String> params = arguments.getParameters();
		if (processor instanceof DataSetAware){
			dataSetMetadata = this.getDataSetMetadata(arguments);
			if (dataSetMetadata != null) {
				logger.debug(
						String.format("[CENTROMERE] Using DataSetMetadata: %s", dataSetMetadata.toString()));
				((DataSetAware) processor).setDataSetMetadata(dataSetMetadata);
			} else {
				logger.warn("[CENTROMERE] Data set metadata is null.");
			}
		}
		if (processor instanceof ImportOptionsAware){
			((ImportOptionsAware) processor).setImportOptions(options);
		}
		if (processor instanceof DataFileAware){
			if (Iterables.size(manager.getDataFileRepository().findByFilePath(input)) == 0){
				BasicDataFileMetadata df = new BasicDataFileMetadata();
				df.setFilePath(input);
				df.setDataType(arguments.getDataType());
				df.setDataSet(dataSetMetadata);
				dataFileMetadata = df;
				logger.debug(String.format("[CENTROMERE] Using DataFileMetadata: %s", dataFileMetadata.toString()));
			} else {
				if (options.isSkipExistingFiles()){
					logger.info(String.format("[CENTROMERE] Skipping existing data file: %s", arguments.getInputFilePath()));
					return;
				} else {
					logger.warn(String.format("Data file already exists: %s", arguments.getInputFilePath()));
					throw new CommandLineRunnerException(String.format("Data file already exists: %s", arguments.getInputFilePath()));
				}
			}
			((DataFileAware) processor).setDataFileMetadata(dataFileMetadata);
		}
		processor.doBefore(params);
		processor.run(input, params);
		processor.doAfter(params);
		logger.debug("[CENTROMERE] Import task complete.");
	}

	/**
	 * Returns reference to a {@link RecordProcessor} instance, assuming it has been registered with
	 *   the {@link DataImportManager} instance.  Throws an exception if no mapping exists.
	 * 
	 * @param dataType String label associated with a particular {@link RecordProcessor}
	 * @return {@link RecordProcessor}
	 * @throws DataImportException
	 */
	private RecordProcessor getProcessorByDataType(String dataType) throws DataImportException{
		if (!manager.isSupportedDataType(dataType)){
			throw new DataImportException(String.format("Unable to identify appropriate RecordProcessor "
					+ "for data type: %s.  This data type may not be registered, or there may not be a bean "
					+ "for the required processor instantiated.", dataType));
		}
		return manager.getDataTypeProcessor(dataType);
	}

	/**
	 * Returns the {@link DataSetMetadata} object passed as a command line argument, or reference to
	 *   an existing object that has been registered with the {@link DataImportManager}.  Throws an
	 *   exception if no metadata can be resolved.
	 * 
	 * @param args {@link ImportCommandArguments} instance, parsed from command line args.
	 * @return {@link DataSetMetadata} object instance.
	 * @throws DataImportException
	 */
	private DataSetMetadata getDataSetMetadata(ImportCommandArguments args) throws DataImportException {
		DataSetMetadata dataSet = args.getDataSetMetadata();
		if (dataSet == null) {
			if (args.getDataSet() != null){
				dataSet = manager.getDataSet(args.getDataSet());
				if (dataSet == null) {
					throw new DataImportException(String.format("DataSet for label does not exist: %s", args.getDataSet()));
				} else if (dataSet.getId() == null) {
					throw new CommandLineRunnerException(
							String.format("DataSet record does not have ID, and may not have "
									+ "been persisted to the database: %s", dataSet.getLabel()));
				}
			}
		}
		return dataSet;
	}
	

}
