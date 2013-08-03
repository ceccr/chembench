<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
<title>CHEMBENCH | Home </title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>
 <style>
<!--
 /* Font Definitions */
 @font-face
	{font-family:SimSun;
	panose-1:2 1 6 0 3 1 1 1 1 1;
	mso-font-alt:\5B8B\4F53;
	mso-font-charset:134;
	mso-generic-font-family:auto;
	mso-font-pitch:variable;
	mso-font-signature:3 680460288 22 0 262145 0;}
@font-face
	{font-family:"Cambria Math";
	panose-1:2 4 5 3 5 4 6 3 2 4;
	mso-font-charset:0;
	mso-generic-font-family:roman;
	mso-font-pitch:variable;
	mso-font-signature:-536870145 1107305727 0 0 415 0;}
@font-face
	{font-family:Calibri;
	panose-1:2 15 5 2 2 2 4 3 2 4;
	mso-font-charset:0;
	mso-generic-font-family:swiss;
	mso-font-pitch:variable;
	mso-font-signature:-520092929 1073786111 9 0 415 0;}
@font-face
	{font-family:Tahoma;
	panose-1:2 11 6 4 3 5 4 4 2 4;
	mso-font-charset:0;
	mso-generic-font-family:swiss;
	mso-font-pitch:variable;
	mso-font-signature:-520081665 -1073717157 41 0 66047 0;}
@font-face
	{font-family:"\@SimSun";
	panose-1:2 1 6 0 3 1 1 1 1 1;
	mso-font-charset:134;
	mso-generic-font-family:auto;
	mso-font-pitch:variable;
	mso-font-signature:3 680460288 22 0 262145 0;}
 /* Style Definitions */
 p.MsoNormal, li.MsoNormal, div.MsoNormal
	{mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-parent:"";
	margin:0in;
	margin-bottom:.0001pt;
	mso-pagination:widow-orphan;
	font-size:12.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";}
h1
	{mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-link:"Heading 1 Char";
	mso-style-next:Normal;
	margin-top:12.0pt;
	margin-right:0in;
	margin-bottom:3.0pt;
	margin-left:0in;
	mso-pagination:widow-orphan;
	page-break-after:avoid;
	mso-outline-level:1;
	font-size:16.0pt;
	font-family:"Arial","sans-serif";
	mso-fareast-font-family:"Times New Roman";
	mso-fareast-theme-font:minor-fareast;
	mso-font-kerning:16.0pt;}
h3
	{mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-link:"Heading 3 Char";
	mso-style-next:Normal;
	margin:0in;
	margin-bottom:.0001pt;
	text-align:justify;
	text-indent:.25in;
	line-height:90%;
	mso-pagination:widow-orphan;
	page-break-after:avoid;
	mso-outline-level:3;
	font-size:12.0pt;
	mso-bidi-font-size:11.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:SimSun;}
p.MsoCommentText, li.MsoCommentText, div.MsoCommentText
	{mso-style-unhide:no;
	mso-style-link:"Comment Text Char1";
	margin:0in;
	margin-bottom:.0001pt;
	mso-pagination:widow-orphan;
	font-size:10.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";}
p.MsoCaption, li.MsoCaption, div.MsoCaption
	{mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-next:Normal;
	margin-top:0in;
	margin-right:0in;
	margin-bottom:10.0pt;
	margin-left:0in;
	mso-pagination:widow-orphan;
	font-size:9.0pt;
	font-family:"Calibri","sans-serif";
	mso-fareast-font-family:"Times New Roman";
	mso-bidi-font-family:"Times New Roman";
	color:#4F81BD;
	font-weight:bold;}
span.MsoCommentReference
	{mso-style-unhide:no;
	mso-ansi-font-size:8.0pt;
	mso-bidi-font-size:8.0pt;}
p.MsoBodyText, li.MsoBodyText, div.MsoBodyText
	{mso-style-name:"Body Text\,Char Char2";
	mso-style-update:auto;
	mso-style-unhide:no;
	mso-style-link:"Body Text Char\,Char Char2 Char";
	margin-top:6.0pt;
	margin-right:0in;
	margin-bottom:3.0pt;
	margin-left:0in;
	text-align:justify;
	text-indent:.3in;
	mso-pagination:widow-orphan;
	font-size:11.0pt;
	font-family:"Arial","sans-serif";
	mso-fareast-font-family:SimSun;
	mso-bidi-font-family:"Times New Roman";}
a:link, span.MsoHyperlink
	{mso-style-unhide:no;
	color:blue;
	text-decoration:underline;
	text-underline:single;}
a:visited, span.MsoHyperlinkFollowed
	{mso-style-unhide:no;
	color:purple;
	mso-themecolor:followedhyperlink;
	text-decoration:underline;
	text-underline:single;}
p.MsoCommentSubject, li.MsoCommentSubject, div.MsoCommentSubject
	{mso-style-unhide:no;
	mso-style-parent:"Comment Text";
	mso-style-link:"Comment Subject Char";
	mso-style-next:"Comment Text";
	margin:0in;
	margin-bottom:.0001pt;
	mso-pagination:widow-orphan;
	font-size:10.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";
	font-weight:bold;}
p.MsoAcetate, li.MsoAcetate, div.MsoAcetate
	{mso-style-noshow:yes;
	mso-style-unhide:no;
	mso-style-link:"Balloon Text Char";
	margin:0in;
	margin-bottom:.0001pt;
	mso-pagination:widow-orphan;
	font-size:8.0pt;
	font-family:"Tahoma","sans-serif";
	mso-fareast-font-family:"Times New Roman";}
p.MsoListParagraph, li.MsoListParagraph, div.MsoListParagraph
	{mso-style-priority:34;
	mso-style-unhide:no;
	mso-style-qformat:yes;
	margin-top:0in;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:.5in;
	margin-bottom:.0001pt;
	mso-add-space:auto;
	mso-pagination:widow-orphan;
	font-size:12.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";}
p.MsoListParagraphCxSpFirst, li.MsoListParagraphCxSpFirst, div.MsoListParagraphCxSpFirst
	{mso-style-priority:34;
	mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-type:export-only;
	margin-top:0in;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:.5in;
	margin-bottom:.0001pt;
	mso-add-space:auto;
	mso-pagination:widow-orphan;
	font-size:12.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";}
p.MsoListParagraphCxSpMiddle, li.MsoListParagraphCxSpMiddle, div.MsoListParagraphCxSpMiddle
	{mso-style-priority:34;
	mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-type:export-only;
	margin-top:0in;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:.5in;
	margin-bottom:.0001pt;
	mso-add-space:auto;
	mso-pagination:widow-orphan;
	font-size:12.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";}
p.MsoListParagraphCxSpLast, li.MsoListParagraphCxSpLast, div.MsoListParagraphCxSpLast
	{mso-style-priority:34;
	mso-style-unhide:no;
	mso-style-qformat:yes;
	mso-style-type:export-only;
	margin-top:0in;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:.5in;
	margin-bottom:.0001pt;
	mso-add-space:auto;
	mso-pagination:widow-orphan;
	font-size:12.0pt;
	font-family:"Times New Roman","serif";
	mso-fareast-font-family:"Times New Roman";}
span.Heading1Char
	{mso-style-name:"Heading 1 Char";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Heading 1";
	mso-ansi-font-size:14.0pt;
	mso-bidi-font-size:14.0pt;
	font-family:"Cambria","serif";
	mso-ascii-font-family:Cambria;
	mso-ascii-theme-font:major-latin;
	mso-fareast-font-family:"Times New Roman";
	mso-fareast-theme-font:major-fareast;
	mso-hansi-font-family:Cambria;
	mso-hansi-theme-font:major-latin;
	mso-bidi-font-family:"Times New Roman";
	mso-bidi-theme-font:major-bidi;
	color:#365F91;
	mso-themecolor:accent1;
	mso-themeshade:191;
	font-weight:bold;}
span.Heading3Char
	{mso-style-name:"Heading 3 Char";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Heading 3";
	mso-ansi-font-size:12.0pt;
	mso-bidi-font-size:11.0pt;
	font-family:SimSun;
	mso-ascii-font-family:SimSun;
	mso-fareast-font-family:SimSun;
	mso-hansi-font-family:SimSun;
	mso-ansi-language:EN-US;
	mso-fareast-language:EN-US;
	mso-bidi-language:AR-SA;
	font-weight:bold;}
span.CommentTextChar
	{mso-style-name:"Comment Text Char";
	mso-style-noshow:yes;
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Comment Text";
	mso-ansi-font-size:10.0pt;
	mso-bidi-font-size:10.0pt;
	font-family:"Times New Roman","serif";
	mso-ascii-font-family:"Times New Roman";
	mso-fareast-font-family:SimSun;
	mso-hansi-font-family:"Times New Roman";
	mso-bidi-font-family:"Times New Roman";
	mso-fareast-language:EN-US;}
span.BodyTextChar
	{mso-style-name:"Body Text Char\,Char Char2 Char";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Body Text\,Char Char2";
	mso-ansi-font-size:11.0pt;
	mso-bidi-font-size:11.0pt;
	font-family:"Arial","sans-serif";
	mso-ascii-font-family:Arial;
	mso-fareast-font-family:SimSun;
	mso-hansi-font-family:Arial;
	mso-bidi-font-family:Arial;
	mso-ansi-language:EN-US;
	mso-fareast-language:EN-US;
	mso-bidi-language:AR-SA;}
span.BodyTextChar1
	{mso-style-name:"Body Text Char1\,Char Char2 Char1";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Body Text\,Char Char2";
	mso-ansi-font-size:12.0pt;
	mso-bidi-font-size:12.0pt;}
span.CommentSubjectChar
	{mso-style-name:"Comment Subject Char";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-parent:"Comment Text Char1";
	mso-style-link:"Comment Subject";
	font-weight:bold;}
span.BalloonTextChar
	{mso-style-name:"Balloon Text Char";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Balloon Text";
	mso-ansi-font-size:8.0pt;
	mso-bidi-font-size:8.0pt;
	font-family:"Tahoma","sans-serif";
	mso-ascii-font-family:Tahoma;
	mso-hansi-font-family:Tahoma;
	mso-bidi-font-family:Tahoma;}
span.CommentTextChar1
	{mso-style-name:"Comment Text Char1";
	mso-style-unhide:no;
	mso-style-locked:yes;
	mso-style-link:"Comment Text";}
span.SpellE
	{mso-style-name:"";
	mso-spl-e:yes;}
span.GramE
	{mso-style-name:"";
	mso-gram-e:yes;}
.MsoChpDefault
	{mso-style-type:export-only;
	mso-default-props:yes;
	font-size:10.0pt;
	mso-ansi-font-size:10.0pt;
	mso-bidi-font-size:10.0pt;}
@page WordSection1
	{size:8.5in 11.0in;
	margin:.5in 558.75pt .5in 36.85pt;
	mso-header-margin:.5in;
	mso-footer-margin:.5in;
	mso-paper-source:0;}
div.WordSection1
	{page:WordSection1;}
-->
</style>
</head>
<body onload="setTabToPPI();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
   <td>
<!--[if gte mso 10]>
<style>
 /* Style Definitions */
 table.MsoNormalTable
	{mso-style-name:"Table Normal";
	mso-tstyle-rowband-size:0;
	mso-tstyle-colband-size:0;
	mso-style-noshow:yes;
	mso-style-priority:99;
	mso-style-qformat:yes;
	mso-style-parent:"";
	mso-padding-alt:0in 5.4pt 0in 5.4pt;
	mso-para-margin:0in;
	mso-para-margin-bottom:.0001pt;
	mso-pagination:widow-orphan;
	font-size:10.0pt;
	font-family:"Times New Roman","serif";}
table.MsoTableGrid
	{mso-style-name:"Table Grid";
	mso-tstyle-rowband-size:0;
	mso-tstyle-colband-size:0;
	mso-style-unhide:no;
	border:solid windowtext 1.0pt;
	mso-border-alt:solid windowtext .5pt;
	mso-padding-alt:0in 5.4pt 0in 5.4pt;
	mso-border-insideh:.5pt solid windowtext;
	mso-border-insidev:.5pt solid windowtext;
	mso-para-margin:0in;
	mso-para-margin-bottom:.0001pt;
	mso-pagination:widow-orphan;
	font-size:10.0pt;
	font-family:"Times New Roman","serif";}
</style>
<![endif]--><!--[if gte mso 9]><xml>
 <o:shapedefaults v:ext="edit" spidmax="3074"/>
</xml><![endif]--><!--[if gte mso 9]><xml>
 <o:shapelayout v:ext="edit">
  <o:idmap v:ext="edit" data="1"/>
 </o:shapelayout></xml><![endif]-->
</head>

<body lang=EN-US link=blue vlink=purple style='tab-interval:.5in'>

<a href="/jchem/marvin/examples/applets/example-sketch1.1.html">JchemSketch</a>
<p></p>	 
<div class=WordSection1>

<p class=MsoNormal><b style='mso-bidi-font-weight:normal'><span
style='font-size:16.0pt;font-family:"Arial","sans-serif";color:#C00000'>Synergistic
application of cheminformatics and computational geometry approaches for
predicting protein-protein interactions.</span></b><span style='font-size:16.0pt;
color:#C00000'><o:p></o:p></span></p>

<p class=MsoNormal><o:p>&nbsp;</o:p></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'>Denis Fourches, PhD (<a
href="mailto:fourches@email.unc.edu">fourches@email.unc.edu</a>), Stephen Bush
(<a href="mailto:sjbush@email.unc.edu">sjbush@email.unc.edu</a>) and Alexander
Tropsha, PhD (<a href="mailto:alex_tropsha@unc.edu">alex_tropsha@unc.edu</a>) <o:p></o:p></span></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'><o:p>&nbsp;</o:p></span></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'>Protein-Protein Interactions (<b
style='mso-bidi-font-weight:normal'>PPIs</b>) play a central role in all major signaling
events that occur in living cells [1<span class=GramE>;2</span>], from DNA
replication to complex, post-translational protein-signaling systems. However,
many if not most pairs of interacting proteins remain unknown [3], and their
discovery presents a key challenge for post-genomic biology. In parallel, short
peptides and their derivatives represent a growing source of chemical probes
for investigating protein function [4] or potential drug candidates that can
specifically interact with a target receptor; elucidation of specific Protein-<span
class=SpellE>PEptide</span> interactions (PPEs) could also serve to map
potential PPI sites on protein surfaces. A critical step in PPI analysis and
prediction is the identification of hot spots [5-8] on protein surfaces
involved in PPIs and PPEs. Currently, computational techniques [9] such as MD
simulations and homology- or template-based modeling constitute the main
bioinformatics methods applied to study PPIs, and despite many recent developments
[10-16], fast and reliable predictions of PPI (or PPE) sites remain a
challenge. Therefore, novel, computer-based methodologies are greatly needed to
accurately predict the location of the most probable PPI hot spots on protein
surfaces and binding poses for PPIs and PPEs.<o:p></o:p></span></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'><o:p>&nbsp;</o:p></span></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'>In this project (Figure 1), we are developing
novel approaches to analyzing PPIs that <i style='mso-bidi-font-style:normal'>employ
a unique combination of concepts at the interface between structural
bioinformatics, computational geometry, and cheminformatics</i>. We are
developing a new family of cheminformatics descriptors of PPIs derived from
Delaunay tessellation of PPI interface termed <b style='mso-bidi-font-weight:
normal'>SNAP3</b> (<span class=SpellE><i style='mso-bidi-font-style:normal'>Simplicial</i></span><i
style='mso-bidi-font-style:normal'> Neighborhood Analysis of Protein-Protein
Packing</i>) that are based on the SNAPP approach developed earlier in our
group [17-18]. We will employ these SNAP3 descriptors to develop both
analytical and <b style='mso-bidi-font-weight:normal'>Quantitative
Structure-Activity Relationship (QSAR)</b>–like [19] scoring functions that
will be employed both for hot spot identification and protein (peptide)
docking. These descriptors are employed as part of novel approaches to (1)
identify hot spot regions on protein surfaces and (2) predict the structure of
protein-protein (or protein-peptide) complexes using novel <span class=SpellE><b
style='mso-bidi-font-weight:normal'>GridDock</b></span> docking method to be developed
in this project. <b style='mso-bidi-font-weight:normal'>The novel approaches
advanced in this project employ a unique combination of concepts at the
interface between structural bioinformatics, computational geometry, and
cheminformatics.</b></span></p>

<p class=MsoNormal style='margin-top:3.0pt;margin-right:912.75pt;margin-bottom:
0in;margin-left:0in;margin-bottom:.0001pt;text-align:justify'><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'><o:p>&nbsp;</o:p></span></p>

<p class=MsoNormal style='text-indent:49.5pt'><b style='mso-bidi-font-weight:
normal'><span style='mso-no-proof:yes'><!--[if gte vml 1]><v:shapetype id="_x0000_t75"
 coordsize="21600,21600" o:spt="75" o:preferrelative="t" path="m@4@5l@4@11@9@11@9@5xe"
 filled="f" stroked="f">
 <v:stroke joinstyle="miter"/>
 <v:formulas>
  <v:f eqn="if lineDrawn pixelLineWidth 0"/>
  <v:f eqn="sum @0 1 0"/>
  <v:f eqn="sum 0 0 @1"/>
  <v:f eqn="prod @2 1 2"/>
  <v:f eqn="prod @3 21600 pixelWidth"/>
  <v:f eqn="prod @3 21600 pixelHeight"/>
  <v:f eqn="sum @0 0 1"/>
  <v:f eqn="prod @6 1 2"/>
  <v:f eqn="prod @7 21600 pixelWidth"/>
  <v:f eqn="sum @8 21600 0"/>
  <v:f eqn="prod @7 21600 pixelHeight"/>
  <v:f eqn="sum @10 21600 0"/>
 </v:formulas>
 <v:path o:extrusionok="f" gradientshapeok="t" o:connecttype="rect"/>
 <o:lock v:ext="edit" aspectratio="t"/>
</v:shapetype><v:shape id="Picture_x0020_29" o:spid="_x0000_i1025" type="#_x0000_t75"
 alt="project_layout" style='width:498pt;height:249pt;visibility:visible;
 mso-wrap-style:square'>
 <v:imagedata src="theme/img/PPI/image001.png" o:title="project_layout"/>
</v:shape><![endif]--><![if !vml]><img border=0 width=664 height=332
src="theme/img/PPI/image001.png" alt="project_layout" v:shapes="Picture_x0020_29"><![endif]></span></b><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'><o:p></o:p></span></p>

<p class=MsoCaption style='margin-top:3.0pt;text-indent:49.5pt'><a
name="_Ref297208956"></a><span class=GramE><span style='mso-bookmark:_Ref297208956'><span
style='font-size:12.0pt;color:black;mso-themecolor:text1'>Figure </span></span></span><!--[if supportFields]><span
style='mso-bookmark:_Ref297208956'></span><span style='mso-element:field-begin'></span><span
style='mso-bookmark:_Ref297208956'><span class=GramE><span style='font-size:
12.0pt;color:black;mso-themecolor:text1'><span
style='mso-spacerun:yes'></span>SEQ Figure \* ARABIC </span><span
style='mso-element:field-separator'></span></span></span><![endif]--><span
style='mso-bookmark:_Ref297208956'></span><span style='mso-bookmark:_Ref297208956'><span
class=GramE><span style='font-size:12.0pt;color:black;mso-themecolor:text1;
mso-no-proof:yes'>1</span></span></span><!--[if supportFields]><span
style='mso-bookmark:_Ref297208956'></span><span style='mso-element:field-end'></span><![endif]--><span
style='mso-bookmark:_Ref297208956'></span><span style='mso-bookmark:_Ref297208956'></span><span
class=GramE><span style='font-size:12.0pt;color:black;mso-themecolor:text1'>.</span></span><span
style='font-size:12.0pt;color:black;mso-themecolor:text1'> Study design of the
entire project.<o:p></o:p></span></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'>In the post-genomic era, analysis and
prediction of PPIs is considered critical for elucidating of many if not most
protein functions to better our understanding of cell biology, cellular
networks and human diseases. This project will provide both the experimental
and computational communities with novel and powerful methodologies and
software for analyzing and predicting PPIs and <a name="_GoBack"></a>enable
several important applications including: (1) unique description and comparison
of PPIs using <span class=SpellE>chemometric</span> approaches; (2) the
identification of unknown PPI hot spots on protein surfaces; and (3) rational
design of novel peptides with desired binding specificity against target
proteins. It will have a broad impact by making all PPI datasets, computational
tools, and models developed herein accessible via a new module within our
publicly available framework, the <span class=SpellE>ChemBench</span> web
portal.<o:p></o:p></span></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'><o:p>&nbsp;</o:p></span></p>

<p class=MsoNormal style='text-align:justify'><i style='mso-bidi-font-style:
normal'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>- Updated
on 7/18/2012 -<o:p></o:p></span></i></p>

<p class=MsoNormal style='text-align:justify'><span style='font-size:11.0pt;
font-family:"Arial","sans-serif"'><o:p>&nbsp;</o:p></span></p>

<p class=MsoNormal style='text-align:justify'><b style='mso-bidi-font-weight:
normal'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>References<o:p></o:p></span></b></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[1] J.
J. Gray, &quot;High-resolution protein-protein docking,&quot; <span
class=SpellE>Curr</span>. <span class=SpellE><span class=GramE>Opin</span></span><span
class=GramE>.</span> <span class=SpellE><span class=GramE>Struct</span></span><span
class=GramE>.</span> <span class=GramE>Biol., vol. 16, no. 2, pp. 183-193,
Apr.2006.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[2] X.
Li, O. <span class=SpellE>Keskin</span>, B. Ma, R. <span class=SpellE>Nussinov</span>,
and J. Liang, &quot;Protein-protein interactions: hot spots and structurally
conserved residues often locate in complemented pockets that pre-organized in
the unbound states: implications for docking,&quot; J. Mol. Biol., vol. 344,
no. 3, pp. 781-795, Nov.2004.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span class=GramE><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[3]
T. <span class=SpellE>Geppert</span>, E. <span class=SpellE>Proschak</span>,
and G. Schneider, &quot;Protein-protein docking by shape-<span class=SpellE>complementarity</span>
and property matching,&quot; J. <span class=SpellE>Comput</span>.</span></span><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'> <span class=GramE>Chem.,
vol. 31, no. 9, pp. 1919-1928, July2010.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span class=GramE><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[4]
T. <span class=SpellE>Aita</span>, K. <span class=SpellE>Nishigaki</span>, and
Y. <span class=SpellE>Husimi</span>, &quot;Toward the fast blind docking of a
peptide to a target protein by using a four-body statistical
pseudo-potential,&quot; <span class=SpellE>Comput</span>.</span></span><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'> Biol. Chem., vol.
34, no. 1, pp. 53-62, Feb.2010.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[5] W.
L. <span class=SpellE><span class=GramE>DeLano</span></span>, &quot;Unraveling
hot spots in binding interfaces: progress and challenges,&quot; <span
class=SpellE>Curr</span>. <span class=SpellE><span class=GramE>Opin</span></span><span
class=GramE>.</span> <span class=SpellE><span class=GramE>Struct</span></span><span
class=GramE>.</span> <span class=GramE>Biol., vol. 12, no. 1, pp. 14-20,
Feb.2002.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[6] E. <span
class=SpellE>Guney</span>, N. <span class=SpellE>Tuncbag</span>, O. <span
class=SpellE>Keskin</span>, and A. <span class=SpellE>Gursoy</span>, &quot;<span
class=SpellE>HotSprint</span>: database of computational hot spots in protein
interfaces,&quot; Nucleic Acids Res., vol. 36, no. <span class=GramE>Database
issue, p. D662-D666, Jan.2008.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[7] O. <span
class=SpellE>Keskin</span>, B. Ma, and R. <span class=SpellE>Nussinov</span>,
&quot;Hot regions in protein--protein interactions: the organization and
contribution of structurally conserved hot spot residues,&quot; J. Mol. Biol.,
vol. 345, no. 5, pp. 1281-1294, Feb.2005.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[8] X.
Li, O. <span class=SpellE>Keskin</span>, B. Ma, R. <span class=SpellE>Nussinov</span>,
and J. Liang, &quot;Protein-protein interactions: hot spots and structurally
conserved residues often locate in complemented pockets that pre-organized in
the unbound states: implications for docking,&quot; J. Mol. Biol., vol. 344,
no. 3, pp. 781-795, Nov.2004.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[9] I.
S. <span class=SpellE>Moreira</span>, P. A. <span class=SpellE>Fernandes</span>,
and M. J. Ramos, &quot;Protein-protein docking dealing with the unknown,&quot;
J. <span class=SpellE>Comput</span>. <span class=GramE>Chem., vol. 31, no. 2,
pp. 317-342, Jan.2010.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[10] J.
<span class=SpellE>Bernauer</span>, J. <span class=SpellE>Aze</span>, J. <span
class=SpellE>Janin</span>, and A. <span class=SpellE>Poupon</span>, &quot;A new
protein-protein docking scoring function based on interface residue properties,<span
class=GramE>&quot; Bioinformatics.,</span> vol. 23, no. 5, pp. 555-562,
Mar.2007.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[11] A.
J. <span class=SpellE>Bordner</span> and R. <span class=SpellE>Abagyan</span>,
&quot;Statistical analysis and prediction of protein-protein interfaces,&quot;
Proteins, vol. 60, no. 3, pp. 353-366, Aug.2005.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[12] J.
J. Gray, S. <span class=SpellE>Moughon</span>, C. Wang, O. <span class=SpellE>Schueler</span>-Furman,
B. Kuhlman, C. A. <span class=SpellE>Rohl</span>, and D. Baker,
&quot;Protein-protein docking with simultaneous optimization of rigid-body
displacement and side-chain conformations,&quot; J. Mol. Biol., vol. 331, no.
1, pp. 281-299, Aug.2003.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[13] E.
<span class=SpellE>Karaca</span>, A. S. <span class=SpellE>Melquiond</span>, S.
J. de <span class=SpellE>Vries</span>, P. L. <span class=SpellE>Kastritis</span>,
and A. M. <span class=SpellE>Bonvin</span>, &quot;Building macromolecular
assemblies by information-driven docking: introducing the HADDOCK <span
class=SpellE>multibody</span> docking server,&quot; Mol. Cell Proteomics., vol.
9, no. 8, pp. 1784-1794, Aug.2010.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[14] D.
<span class=SpellE>Kozakov</span>, O. <span class=SpellE>Schueler</span>-Furman,
and S. <span class=SpellE>Vajda</span>, &quot;Discrimination of near-native
structures in protein-protein docking by testing the stability of local
minima,&quot; Proteins, vol. 72, no. 3, pp. 993-1004, Aug.2008.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[15] I.
S. <span class=SpellE>Moreira</span>, P. A. <span class=SpellE>Fernandes</span>,
and M. J. Ramos, &quot;Protein-protein docking dealing with the unknown,&quot;
J. <span class=SpellE>Comput</span>. <span class=GramE>Chem., vol. 31, no. 2,
pp. 317-342, Jan.2010.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-left:.25in;text-align:justify;text-indent:
-.25in'><span class=GramE><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[16]
E. <span class=SpellE>Noy</span> and A. <span class=SpellE>Goldblum</span>,
&quot;Flexible protein-protein docking based on Best-First search
algorithm,&quot; J. <span class=SpellE>Comput</span>.</span></span><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'> <span class=GramE>Chem.,
vol. 31, no. 9, pp. 1929-1943, July2010.</span><o:p></o:p></span></p>

<p class=MsoNormal style='margin-top:0in;margin-right:-5.25pt;margin-bottom:
0in;margin-left:.25in;margin-bottom:.0001pt;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[17] B.
<span class=SpellE>Krishnamoorthy</span> and A. Tropsha, &quot;Development of a
four-body statistical pseudo-potential to discriminate native from non-native
protein conformations,<span class=GramE>&quot; Bioinformatics.,</span> vol. 19,
no. 12, pp. 1540-1548, Aug.2003.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-top:0in;margin-right:-5.25pt;margin-bottom:
0in;margin-left:.25in;margin-bottom:.0001pt;text-align:justify;text-indent:
-.25in'><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[18] A.
Tropsha, C. W. Carter, Jr., S. <span class=SpellE>Cammer</span>, and I. I. <span
class=SpellE>Vaisman</span>, &quot;<span class=SpellE>Simplicial</span>
neighborhood analysis of protein packing (SNAPP): a computational geometry
approach to studying proteins,&quot; Methods <span class=SpellE>Enzymol</span>.,
vol. 374, pp. 509-544, 2003.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-top:0in;margin-right:-5.25pt;margin-bottom:
0in;margin-left:.25in;margin-bottom:.0001pt;text-align:justify;text-indent:
-.25in'><span class=GramE><span style='font-size:11.0pt;font-family:"Arial","sans-serif"'>[19]
A. Tropsha and A. Golbraikh, &quot;Predictive QSAR modeling workflow, model
applicability domains, and virtual screening,&quot; <span class=SpellE>Curr</span>.</span></span><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'> Pharm. Des, vol. 13,
no. 34, pp. 3494-3504, 2007.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-right:-5.25pt;text-align:justify'><span
style='font-size:11.0pt;font-family:"Arial","sans-serif"'><o:p>&nbsp;</o:p></span></p>
</td>
</tr>
</table>
</div>
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
