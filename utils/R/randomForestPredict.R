options(error=traceback)
# parameters:
# scriptsDir: path of the R scripts directory
# workDir: path of the working directory
# modelsListFile: the model list file
# xFile: path of the x file to predict

##First read in the arguments listed at the command line
args = commandArgs(TRUE)

if (length(args) != 0) {
    for (i in seq(1, length(args), by=2)) {
        assign(sub("--", "", args[i]), args[i+1])
    }
} else {
    cat("No args\n")
}

# check mandatory parameters
if (!exists("scriptsDir")) {
    stop("scriptsDir: missing parameter, the R scripts directory path must be entered.")
}
if (!exists("workDir")) {
    stop("workDir: missing parameter, the working directory path must be entered.")
}
if (!exists("modelsListFile")) {
    stop("modelsListFile: missing parameter, the models list file must be entered.")
}
if (!exists("xFile")) {
    stop("xFile: missing parameter, the x file path must be entered.")
}

# load the randomForest library
library("randomForest")

cat("\n")
cat("load readXfile script\n")
readXfileScript = paste(scriptsDir, "readXfile.R", sep="")
source(readXfileScript)

# external set
cat("reading the x file to predict\n")
# copy the X file, snip the last two lines, then use that instead
# (last two lines are statistical information used for modeling;
#  we don't want these to be considered as compounds)
#system(paste("head --lines=-2 <", 
#             paste(workDir, xFile, sep=""),
#             ">", paste0(workDir, xFile, ".snip")))
# now our new file is called "<xfile>.snip"; use that
#data = readXfile(paste0(workDir, xFile, ".snip"), TRUE)
data = readXfile(paste0(workDir, xFile), TRUE)

cat("reading the models list file\n")
modelsList = read.table(paste(workDir, modelsListFile, sep=""), quote="",comment.char="")

models = as.vector(modelsList[,1])

prediction = matrix(nrow = nrow(data), ncol = length(models), dimnames = list(rownames(data), models))

for (model in models) {
    load(paste(workDir, model, ".RData", sep=""))
    type = eval(parse(text=paste(model, "type", sep="$")))
    if (type == "regression") {
        paramTmp = paste("alist(", model, ", data)", sep="")
        param = eval(parse(text=paramTmp))

        pred = do.call(predict, param)

        prediction[,model] = as.vector(pred)
    } else {
        if (type == "classification") {
            paramTmp = paste("alist(", model, ", data, type=\"vote\", norm.votes=TRUE)", sep="")
            param = eval(parse(text=paramTmp))

            pred = do.call(predict, param)

            v = vector(mode="numeric", length=nrow(data))
            names(v) = rownames(data)
            for (i in colnames(pred))
            {
                tmpV = pred[,i]*as.integer(i)
                v = v+tmpV
            } 
            prediction[,model] = v
        }
    }
}

cat("\n")
predFile = paste(workDir, "cons_pred.preds", sep="")
write.table(prediction, file=predFile, quote = FALSE, sep = "\t", row.names = TRUE, col.names = NA)
cat("prediction matrix saved into", predFile, "\n", sep=" ")

q(status=0)

