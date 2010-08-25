<%@include file ="/jsp/help/helpheader.jsp" %>


<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
 <tr><td>
<div class="ccbHomeStandard">
	<p>
	Chembench is a web-based tool for predicting the properties of chemical compounds. 
	We can predict if a compound is toxic, or if it will make a good drug to treat seizures or malaria, 
	or if it will make a good perfume, etc. using the methods we support.
	</p> 
	<p>
	To make these predictions, we first use Chembench to build predictive models.
	If you have a set of compounds where you know the activity already, you can use them to
	build a model.  For example, we have a dataset of several chemicals that have been
	tested for their activity against malaria. A user can take that dataset and feed it to
	one of our model creation algorithms to produce a model. The antimalarial model can then
	be used to predict what other compounds might treat malaria. And you can do this for 
	any chemical property. This is what our lab's work is - we build models of chemical properties.
	</p>
	<p>
	The most important part of building a good model is crafting a good dataset. If a dataset
	has problems in it, such as a few compounds that are vastly different from the other compounds
	in the dataset, it can wreck the performance of the modeling algorithms. The algorithms are 
	good, but like anything in machine learning, if you put garbage in, you get garbage out.
	</p>
	<p>
	Right now, users don't have the tools they need to help them analyze their datasets and find potential 
	problems. We'd like to add ways for users to visualize their datasets and their models -- and that's where 
	(we hope!) you come in.
	</p>
	<p>
	Here's what we have right now:
	<img src="/jsp/help/images/heatMap.jpg">
	<br />
	<img src="/jsp/help/images/externalSetGraph.jpg">
	</p>
	<p>
	We'd love to make the model graph more interactive, so that you'd be able to see the structures for
	the outlier compounds. And we'd like to have the heatmap redone in HTML5 / JQuery, with a few more 
	features, so it'll be more usable and more maintainable. 
	</p>

</div></td></tr></table>

	<%@include file ="/jsp/main/footer.jsp" %>
</body>