<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="/t-tags"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
</script>
</head>
<body>
<table style="width: 100%;">
	<c:if test="${not empty buyHisList}">
	<c:forEach items="${buyHisList}" var="var" varStatus="st">
	<tr style="width: 100%;border:1px solid #cccccc;">
		<td align="left" style="width: 30px;">&nbsp;</td>
		<td>
			<table style="width: 100%;border-collapse:separate;border-spacing:0px 10px;">
			<tr>
				<td align="left" style="width: 60px;" colspan="2">ECS（${var.orderNo}）</td>
				<td align="right" style="padding: 10px;">${var.virNum}台</td>
			</tr>
			<tr>
				<td align="left" style="width: 60px;">地域：</td>
				<td align="left" colspan="2">${var.areaCodeName}</td>
			</tr>
			<tr>
				<td align="left" style="width: 60px;">资源类型：</td>
				<td align="left" colspan="2">${var.resTypeName}</td>
			</tr>
			<tr>
				<td align="left" style="width: 60px;">实例规格：</td>
				<td align="left" colspan="2">${var.cpu}&nbsp;核&nbsp;${var.memory}&nbsp;GB</td>
			</tr>
			<tr>
				<td align="left">镜像：</td>
				<td align="left" colspan="2">${var.osTypeName}&nbsp;${var.osBitNumName}</td>
			</tr>
			<tr>
				<td align="left" valign="top">数据盘：</td>
				<td align="left" valign="top" colspan="2">
					<t:list key="${var.diskTypeName}" val="${var.diskSize}" name="vars">
						${vars.dictCode}&nbsp;(&nbsp;${vars.dictValue}&nbsp;GB)<br>
					</t:list>
				</td>
			</tr>
			<tr>
				<td align="left">购买量：</td>
				<td align="left" colspan="2">${var.virNum}&nbsp;台</td>
			</tr>
			<tr>
				<td align="left">到期时间：</td>
				<td align="left" colspan="2">${var.expireDate}</td>
			</tr>
			</table>
		</td>
	</tr>
	</c:forEach>
	</c:if>
</table>
<script type="text/javascript">
$("#buyHisNum").html("${buyHisNum}");
</script>
</body>
</html>