options(error = traceback)
# parameters:
# scriptsDir: path of the R scripts directory
# workDir: path of the working directory
# externalXFile: the external x file
# dataSplitsListFile: path of the file with list of data split files
# type: classification or regression

# randomForest arguments (see: http://127.0.0.1:31659/library/randomForest/html/randomForest.html)
# ntree: (integer) Number of trees to grow
# mtry: (integer) Number of variables randomly sampled as candidates at each split.
# classwt: (vector) Priors of the classes. Need not add up to one. Ignored for regression.
# nodesize: (integer) Minimum size of terminal nodes.
# maxnodes: (integer) Maximum number of terminal nodes trees in the forest can have.

# hard coded parameters
# replace=FALSE
# cutoff=if(is.factor(activity)) rep(1/length(levels(activity)), length(levels(activity))) else NULL
# strata=NULL
# sampsize=nrow(x)
# importance=FALSE
# localImp=FALSE
# nPerm=1
# proximity=FALSE
# oob.prox=FALSE
# norm.votes=FALSE
# do.trace=FALSE
# keep.forest=TRUE
# corr.bias=FALSE
# keep.inbag=FALSE

##First read in the arguments listed at the command line
args = commandArgs(TRUE)

if (length(args) != 0)
{
  for (i in seq(1, length(args), by = 2))
  {
    assign(sub("--", "", args[i]), args[i + 1])
  }
}else
{
  cat("No args\n")
}

# check mandatory parameters
if (!exists("scriptsDir"))
{
  stop("scriptsDir: missing parameter, the R scripts directory path must be entered.")
}
if (!exists("workDir"))
{
  stop("workDir: missing parameter, the working directory path must be entered.")
}
if (!exists("externalXFile"))
{
  stop("externalXFile: missing parameter, the external X file must be entered.")
}
if (!exists("dataSplitsListFile"))
{
  stop(
    "dataSplitsListFile: missing parameter, the file with list of data split files must be entered."
  )
}
if (!exists("type"))
{
  stop("type: missing parameter, the type must be entered.")
}

# check randomForest arguments
if (!exists("ntree"))
{
  stop("ntree: missing parameter, the number of trees to grow must be entered.")
}else
{
  ntree = as.integer(ntree)
}

# FIXME should use default if not provided
#if(!exists("mtry"))
#{
#  stop("mtry: missing parameter, the number of variables randomly sampled as candidates at each split must be entered.")
#}else
#{
#  mtry = as.integer(mtry)
#}

if (!exists("classwt"))
{
  stop("classwt: missing parameter, the priors of the classes must be entered")
}else
{
  classwt = eval(parse(text = classwt))
}

# FIXME should use default if not provided
#if(!exists("nodesize"))
#{
#  stop("nodesize: missing parameter, the minimum size of terminal nodes must be entered")
#}else
#{
#  nodesize = as.integer(nodesize)
#}

if (!exists("maxnodes"))
{
  stop(
    "maxnodes: missing parameter, the Maximum number of terminal nodes trees in the forest can have must be entered"
  )
}else
{
  if (maxnodes == "NULL")
  {
    maxnodes = eval(parse(text = maxnodes))
  }else
  {
    maxnodes = as.integer(maxnodes)
  }
}


# load the randomForest library
library("randomForest")

cat("\n")
cat("load readXfile script\n")
readXfileScript = paste(scriptsDir, "readXfile.R", sep = "")
source(readXfileScript)

# data split
dataSplitsList = read.table(paste(
  workDir, dataSplitsListFile, sep = "", quote = "",comment.char = ""
))

# external set
cat("reading the external X file\n")
externalDesc = readXfile(paste(workDir, externalXFile, sep = ""),TRUE)
externalPred = matrix(
  nrow = nrow(externalDesc), ncol = nrow(dataSplitsList), dimnames = list(rownames(externalDesc), sub("[.]x$", "", dataSplitsList[,1]))
)
cat("done reading external X file\n")

models = vector(mode = "character", length = nrow(dataSplitsList))

descriptorsUsedFile = paste(workDir, "descriptors_used_in_models.txt", sep =
                              "")

cat("making forests\n")
for (model in 1:nrow(dataSplitsList))
{
  # data split training set
  dataSplitXFile = paste(workDir, dataSplitsList[model,1], sep = "")
  print(dataSplitXFile)
  x = readXfile(dataSplitXFile, TRUE)
  print(nrow(x))

  dataSplitActFile = paste(workDir, dataSplitsList[model,2], sep = "")
  cat("data split act file: ", dataSplitActFile, "\n")
  dataSplitAct <-
    read.table(
      dataSplitActFile, row.names = 1, col.names = c("","activity"), quote = "",comment.char =
        ""
    )

  # data split external set
  dataSplitExtXfile = paste(workDir, dataSplitsList[model,4], sep = "")
  xtest = readXfile(dataSplitExtXfile, TRUE)

  dataSplitExtActFile = paste(workDir, dataSplitsList[model,5], sep =
                                "")
  dataSplitExtAct <-
    read.table(
      dataSplitExtActFile ,row.names = 1, col.names = c("","activity"), quote =
        "",comment.char = ""
    )

  y = dataSplitAct$activity
  names(y) = rownames(dataSplitAct)
  ytest = dataSplitExtAct$activity
  names(ytest) = rownames(dataSplitExtAct)

  if (type == "classification")
  {
    activity = as.factor(c(y, ytest))
  }else
  {
    activity = c(y, ytest)
  }

  print(nrow(x))
  print(length(y))

  rf = randomForest(
    x = x,
    y = activity[names(y)],
    xtest = xtest,
    ytest = activity[names(ytest)],
    ntree = ntree,
    #mtry=mtry, FIXME should use default if not provided
    replace = FALSE,
    classwt = classwt,
    cutoff = if (is.factor(activity))
      rep(1 / length(levels(activity)), length(levels(activity)))
    else
      NULL,
    strata = NULL,
    sampsize = nrow(x),
    #nodesize=nodesize, FIXME should use default if not provided
    maxnodes = maxnodes,
    importance = TRUE,
    localImp = FALSE,
    nPerm = 1,
    proximity = FALSE,
    oob.prox = FALSE,
    norm.votes = FALSE,
    do.trace = FALSE,
    keep.forest = TRUE,
    corr.bias = FALSE,
    keep.inbag = FALSE
  )
  write.table(
    rf$test$predicted, file = sub("[.]x$", ".pred", dataSplitXFile), quote = FALSE, sep = "\t", col.names = FALSE
  )


  if (type == "classification")
  {
    pred = predict(rf, externalDesc, type = "vote", norm.votes = TRUE)
    v = vector(mode = "numeric", length = nrow(externalDesc))
    names(v) = rownames(externalDesc)
    for (i in colnames(pred))
    {
      tmpV = pred[,i] * as.integer(i)
      v = v + tmpV
    }
    externalPred[,model] = v

    errRateFile = sub("[.]x$", ".err.rate", dataSplitXFile)
    write.table(
      rf$test$err.rate, file = errRateFile, quote = FALSE, sep = "\t", row.names = TRUE, col.names = NA
    )
    cat("err.rate values saved into", errRateFile, "\n", sep = " ")

    confusionFile = file = sub("[.]x$", ".confusion", dataSplitXFile)
    write.table(
      rf$test$confusion, confusionFile, quote = FALSE, sep = "\t", row.names = TRUE, col.names = NA
    )
    cat("confusion matrix saved into", confusionFile, "\n", sep = " ")

    votesFile = sub("[.]x$", ".votes", dataSplitXFile)
    write.table(
      rf$test$votes, file = votesFile, quote = FALSE, sep = "\t", row.names = TRUE, col.names = NA
    )
    cat("votes saved into", votesFile, "\n", sep = " ")
  }else
  {
    externalPred[,model] = as.vector(predict(rf, externalDesc))

    mseFile = sub("[.]x$", ".mse", dataSplitXFile)
    write.table(
      rf$test$mse, file = mseFile, quote = FALSE, sep = "\t", row.names = FALSE, col.names = FALSE
    )
    cat("mse values saved into", mseFile, "\n", sep = " ")

    rsqFile = sub("[.]x$", ".rsq", dataSplitXFile)
    write.table(
      rf$test$rsq, file = rsqFile, quote = FALSE, sep = "\t", row.names = FALSE, col.names = FALSE
    )
    cat("rsq values saved into", rsqFile, "\n", sep = " ")
  }

  models[[model]] = sub("[.]x$", "", dataSplitsList[model,1])

  descriptorsInTreeFile = sub("[.]x$", "_desc_used_in_trees.txt", dataSplitXFile)
  descriptorsUsed = vector()
  trees = vector(mode = "character", length = rf$ntree)
  for (i in 1:rf$ntree)
  {
    trees[[i]] = paste(models[[model]],"_tree_", i, ".tree", sep = "")
    treeFile = sub("[.]x$", paste("_tree_", i, ".tree", sep = ""), dataSplitXFile)
    tree = getTree(rf, k = i, labelVar = TRUE)
    write.table(
      tree, file = treeFile, quote = FALSE, sep = "\t", row.names = TRUE, col.names = NA, na =
        ""
    )

    descriptorsInTree = as.vector(levels(tree[,"split var"]))
    cat(
      descriptorsInTree, file = descriptorsInTreeFile, sep = " ", append = TRUE
    )
    cat("\n", file = descriptorsInTreeFile, sep = "", append = TRUE)
    descriptorsUsed = c(descriptorsUsed, descriptorsInTree)
  }
  cat("descriptors used in each trees saved into", descriptorsInTreeFile, "\n", sep =
        " ")

  treesList = sub("[.]x$", "_trees.list", dataSplitXFile)
  write.table(
    trees, file = treesList, quote = FALSE, row.names = FALSE, col.names = FALSE
  )
  cat("list of tree files saved into", treesList, "\n", sep = " ")

  cat(models[[model]], "\t", file = descriptorsUsedFile, sep = "", append =
        TRUE)
  cat(sort(unique(descriptorsUsed)), file = descriptorsUsedFile, sep =
        " ", append = TRUE)
  cat("\n", file = descriptorsUsedFile, sep = "", append = TRUE)

  assign(eval(models[[model]]), rf)
  modelFile = sub(".x$", ".RData", dataSplitXFile)
  paramTmp = paste("alist(", models[[model]], ", file=modelFile)", sep =
                     "")
  param = eval(parse(text = paramTmp))
  do.call(save, param)
  cat("model", model, "saved into", modelFile, "\n", sep = " ")
  cat("\n")
}
cat("descriptors used in each model saved into", descriptorsUsedFile, "\n", sep =
      " ")

modelsList = paste(workDir, "models.list", sep = "")
write.table(
  models, file = modelsList, quote = FALSE, row.names = FALSE, col.names = FALSE
)
cat("models list saved into", modelsList, "\n", sep = " ")
predFile = paste(workDir, sub("[.]x$", ".pred", externalXFile), sep = "")
write.table(
  externalPred, file = predFile, quote = FALSE, sep = "\t", row.names = TRUE, col.names = NA
)
cat("prediction matrix saved into", predFile, "\n", sep = " ")
