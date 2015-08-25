### if isNormalized=FALSE, will remove descriptors with sd==0, and 
# normalize descriptor values by min, max and range

readXfile <- function(path, isNormalized=TRUE) {
    cat("Reading X file, path: ", path, "\n")
    descriptor_profile = read.table(path, skip=2, quote="", comment.char="")
    cat("Read profile")
    descriptor_info = as.numeric(read.table(path, nrows=1, quote="", comment.char=""))
    cat("Read info")
    descriptor_names = as.character(read.table(path, nrows=1, skip=1, quote="", comment.char="", stringsAsFactors=FALSE))
    cat("Read names")

    dim(descriptor_profile)
    dim(descriptor_info)

    descriptor_profile_t = descriptor_profile[,-c(1,2)]
    if (ncol(descriptor_profile_t) == 1) {
        descriptor_profile_t = t(descriptor_profile_t)
    }
    cat("ncol desc names: ", ncol(descriptor_names), " ncol desc_profile_t: ", ncol(descriptor_profile_t), "\n")

    colnames(descriptor_profile_t) = descriptor_names
    rownames(descriptor_profile_t) = descriptor_profile[,2]
    descriptor_profile = descriptor_profile_t

    ### normalized:
    if (!isNormalized) { 
        key = c()
        for (i in 1:ncol(descriptor_profile)) {
            range = max(descriptor_profile[,i]) - min(descriptor_profile[,i])
            descriptor_profile[,i]=(descriptor_profile[,i]-min(descriptor_profile[,i]))/range

            #### remove descriptors with sd==0
            if (sd(descriptor_profile[,i]) == 0) { 
                key = c(key,i)
            }
        }
        if (key != NULL) {
            descriptor_profile = descriptor_profile[,-key]
        }
    }
    return(descriptor_profile)
}

