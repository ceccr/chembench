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
eval(parse(text = paste("rf", "<-", sub(".RData", "", file))))
write.csv(rf$importance)
