normvec = function(x)
{sqrt(x%*%x)}

similarity.calc=function(i,j,d,temp,method){
	temp[1,]= d[i,]
	temp[2,]= d[j,]
	S=cov(temp)
	matrix(temp, ncol=length(temp[1,]))
	if(method=="m"){
        sqrt(mahalanobis(as.numeric(temp[1,]), as.numeric(temp[2,]),S,inverted=TRUE))
	}
	else if(method=="e"){
  	 	similarity.jaccard(as.numeric(temp[1,]), as.numeric(temp[2,]), mode = "all", loops = FALSE) 
      	  t= as.numeric(temp[1,])%*%as.numeric(temp[2,])
	        a = normvec(as.numeric(temp[1,]))^2
      	  b = normvec(as.numeric(temp[2,]))^2
	        t/(a + b -  t)
	}
}

writenode=function(name){
        write(paste(paste('<node id="',name,sep = "", collapse = NULL),'">',sep = "", collapse = NULL), file = path3,ncolumns = 1,append = TRUE, sep = "\n")
        write(paste(paste('<data key="name">',name,sep = "", collapse = NULL),'</data>',sep = "", collapse = NULL), file = path3,ncolumns = 1,append = TRUE, sep = "\n")
        write('</node>', file = path3,ncolumns = 1,append = TRUE, sep = "\n")
}

writeemptynode=function(name){
        write(paste(paste('<node id="',name,sep = "", collapse = NULL),'">',sep = "", collapse = NULL), file = path3,ncolumns = 1,append = TRUE, sep = "\n")
        write('<data key="name"></data>', file = path3,ncolumns = 1,append = TRUE, sep = "\n")
        write('</node>', file = path3,ncolumns = 1,append = TRUE, sep = "\n")
}

writeedge = function(source, target){
        s1 = paste('<edge source="',source,sep = "", collapse = NULL)
        s2 = paste(s1,'" target="',sep = "", collapse = NULL)
        s3 = paste(s2,target,sep = "", collapse = NULL)
        s4 = paste(s3,'"></edge>',sep = "", collapse = NULL)
        write(s4, file = path3,ncolumns = 1,append = TRUE, sep = "\n")
}

args <- commandArgs(TRUE)
path1 = args[1]
path2 = args[2]
path3 = args[3] 
method = args[4]

print(path1)
print(path2)
print(path3)
print(method)

d = as.matrix(read.table(path1, skip=2,comment.char = ""))
names = d[,2]
result.matrix.numeric = matrix(ncol=length(names),nrow=length(names))
result.matrix = matrix(ncol=length(names)+1,nrow=length(names)+1)
d = d[,-1:-2]
temp = array(dim=c(2,length(d[1,])))
        for(i in 1:length(d[,1])){
                for(j in 1:length(d[,1])){
                                result.matrix[i+1,j+1] = similarity.calc(i,j,d,temp,method)
                                result.matrix.numeric[i,j] = similarity.calc(i,j,d,temp,method)
                }
}

#normalizing mahalanobis
if(method=="m"){
        max1 = max(result.matrix.numeric)
        for(i in 1:length(d[,1])){
                for(j in 1:length(d[,1])){
                        result.matrix.numeric[i,j] = 1-result.matrix.numeric[i,j]/max1
                        result.matrix[i+1,j+1] = result.matrix.numeric[i,j]
                }
        }
}

for(k in 1:length(names)){
                result.matrix[1,k+1] = names[k]
                result.matrix[k+1,1] = names[k]
        }
        result.matrix[1,1] = ""
write(result.matrix, file = path2,ncolumns = length(result.matrix[1,]),
        append = FALSE, sep = "\t")

h = as.dist(result.matrix.numeric)
cl = hclust(h)
merg = cl$merge

header = '<?xml version="1.0" encoding="UTF-8"?>
<graphml xmlns="http://graphml.graphdrawing.org/xmlns"><graph edgedefault="undirected">
<key id="name" for="node" attr.name="name" attr.type="string"/>
<!-- nodes -->'

write(header, file = path3,ncolumns = 1,append = FALSE, sep = "\n")

for(i in 1:length(names)){
        writenode(names[i]);
}
for(i in 2:length(names)-1){
        temp = paste('****',as.character(i),sep = "", collapse = NULL)
        writeemptynode(temp)
        if(merg[i,1]<0){ writeedge(temp,names[abs(merg[i,1])])}
        if(merg[i,2]<0){ writeedge(temp,names[abs(merg[i,2])])}
        if(merg[i,1]>=0){ writeedge(temp,paste('****',as.character(merg[i,1]),sep = "", collapse = NULL))}
        if(merg[i,2]>=0){ writeedge(temp,paste('****',as.character(merg[i,2]),sep = "", collapse = NULL))}
}