### if isNormalized=FALSE, will remove descriptors with sd==0, and 
# normalize descriptor values by min, max and range


readXfile<-function(path,isNormalized=TRUE)
{
descriptor_profile=as.matrix(read.table(path,skip=2, quote=""))
descriptor_info=as.matrix(read.table(path,nrows=1, quote=""))
descriptor_names=as.matrix(read.table(path,nrows=1,skip=1, quote=""))
dim(descriptor_profile)
dim(descriptor_info)


descriptor_profile_t=matrix(0,descriptor_info[1],descriptor_info[2])

for ( i in 1:ncol(descriptor_names))
{
   cat(descriptor_names[,i], "\n")
}


descriptor_profile_t=as.matrix(descriptor_profile[,-c(1,2)])
if(ncol(descriptor_profile_t)==1){
  descriptor_profile_t=t(descriptor_profile_t)
}
cat(descriptor_profile_t, "\n", descriptor_names, "\n")
cat("ncol desc names: ", ncol(descriptor_names), " ncol desc_profile_t: ", ncol(descriptor_profile_t), "\n")

colnames(descriptor_profile_t)=descriptor_names
rownames(descriptor_profile_t)=descriptor_profile[,2]
descriptor_profile=descriptor_profile_t

### normalized:
if(!isNormalized)
{ key=c()
for ( i in 1:ncol(descriptor_profile))
{
 range=max(descriptor_profile[,i])-min(descriptor_profile[,i])
 descriptor_profile[,i]=(descriptor_profile[,i]-min(descriptor_profile[,i]))/range
 
 #### remove descriptors with sd==0
 if(sd(descriptor_profile[,i])==0)
    { key=c(key,i)
      }
}
if (key!=NULL)
	{
	descriptor_profile=descriptor_profile[,-key]
	}

}

return(descriptor_profile)

}
