<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Help | File Formats</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Help &amp; Resources</h2>
    <hr>
    <div class="row">
      <div class="col-xs-3">
        <%@ include file="help-nav.jsp" %>
      </div>

      <section id="help-content" class="col-xs-9">
        <h3>File Formats</h3>
        <p>Datasets uploaded to Chembench are expected to contain these types of files.</p>

        <h3 id="ACT">Activity (<code>.act</code>) Files</h3>
        <ul>
          <li><a href="${pageContext.request.contextPath}/assets/samples/continuousActFileSample.act" target="_blank">Sample
            with continuous activities</a></li>
          <li><a href="${pageContext.request.contextPath}/assets/samples/categoryActFileSample.act" target="_blank">Sample
            with categorical activities</a></li>
        </ul>

        <p>The <code>.act</code> file stores activities of the dataset's compounds from some assay. An activity file is
          necessary for building predictive models on Chembench. Each line of an activity file is a chemical identifier
          followed by an activity value. Activity files may contain continuous or category data.
        </p>

        <p>
          The chemical identifiers in an activity file may be anything: SMILES strings, chemical names, and index
          numbers are commonly used. The only constraint is that the chemical identifiers in your activity file <strong>must
          match</strong> the identifiers in <code>.sdf</code> and <code>.x</code> files uploaded in the same dataset and
          be in the same order.
        </p>

        <p><b>Continuous</b> activity data can be any decimal number. Typically continuous data comes from quantitative
          assays, e.g., of binding affinity. An example of a continuous activity file:
        </p>
<pre>
compound1 2.48
compound2 4.89
compound3 7.22
compound4 9.73
compound5 12.19
compound6 14.55
compound7 17.34
...
</pre>

        <p><b>Category</b> activity data represents endpoints or is discretized from continuous data. Category
          activities are typically non-negative consecutive integers (e.g. 0, 1, 2). An example of a category activity
          file:
        </p>
<pre>
compound1 0
compound2 0
compound3 1
compound4 1
compound5 1
compound6 2
compound7 2
...
</pre>

        <h3 id="SDF">Structure (<code>.sdf</code>) Files</h3>
        <ul>
          <li><a href="${pageContext.request.contextPath}/assets/samples/sdfFileSample.sdf" target="_blank">Sample
            file</a></li>
        </ul>
        <p>The <code>.sdf</code> file stores the structures of the compounds in the dataset. An example:</p>

<pre>
compound1
comment line (can be anything)

44 47  0  0  1  0  0  0  0  0999 V2000
  1.3550   -4.8300    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1.0920   -3.9960    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
  0.4780   -2.3340    0.0000 C   0  0  3  0  0  0  0  0  0  0  0  0
  ...
  1.0240    3.0240    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
  0.5970    4.3590    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  2.1340    2.1230    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
1  2  1  0  0  0  0
2  3  1  0  0  0  0
3  4  1  0  0  0  0
...
41 42  1  0  0  0  0
41 44  1  0  0  0  0
42 43  1  0  0  0  0
M  END
$$$$
...
</pre>

        <p>Note that each compound is terminated by the sequence <code>$$$$</code>.</p>

        <h3 id="X">Matrix (<code>.x</code>) files</h3>
        <ul>
          <li><a href="${pageContext.request.contextPath}/assets/samples/xFileSample.x" target="_blank">Sample file</a>
          </li>
        </ul>

        <p>The <code>.x</code> file is a descriptor file format used by Chembench. It is similar to the matrix format
          accepted by other data mining programs. It contains a matrix of compounds and their descriptor values. All
          descriptor values must be numeric. The format is described below.
        </p>

        <pre>[LINE 1]: 120 50</pre>
        <p>This header line indicates that a 120 by 50 matrix follows: There are 120 compounds, each with 50 descriptor
          values.
        </p>

        <pre>[LINE 2]: descriptor1 descriptor2 descriptor3...</pre>

        <p>The second line contains the names of the descriptors.</p>

<pre>
[LINE 3]: 1 compound1 0.5 0.609756 0.5625 ...
[LINE 4]: 2 compound2 0 0 0.0208333 0.142857 ...
[LINE 5]: 3 compound3 0 0 0.0208333 0.142857 ...
...
</pre>

        <p>From the third line on, each line represents one compound. The first value on each line is an index, starting
          at 1. The second value is an ID for the compound that matches with the IDs in the corresponding SDF and ACT
          files. The remaining numbers are the values of the descriptors for the compound.
        </p>
      </section>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/help.js"></script>
</body>
</html>
