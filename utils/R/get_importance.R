#!/usr/bin/env Rscript
#
# get_importance.R
#
# A simple script that extracts importance information from a randomForest
# object contained in a Chembench-created RData file (specified by the first
# positional argument) and writes it as a CSV.
#
# Input: positional argument indicating where the RData file is located
# Output: importance data in CSV-format written to stdout
file <- commandArgs(trailingOnly = TRUE)[1]
if (is.na(file)) {
  stop("No file argument specified.")
}

# RData file contains single object with the same name as the filename
load(file)
varname <- sub(".RData", "", tail(strsplit(file, "/")[[1]], n = 1))
eval(parse(text = paste("rf", "<-", varname)))
write.table(rf$importance, quote = FALSE, sep = "\t", col.names = NA)
