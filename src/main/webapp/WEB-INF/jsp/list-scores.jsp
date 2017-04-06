<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
    	<title>Golf Score App</title>
        <link rel="stylesheet" href="../../css/bootstrap.min.css">
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>   		
        <script src="../../js/bootstrap.min.js"></script>               
    </head>

    <body>          
        <div class="container">
	    <ul class="nav nav-tabs">
	    	<li><a href="/" class="glyphicon glyphicon-home">  Home</a></li>
	    	<li><a href="/course"><span class="glyphicon glyphicon-flag">  Courses</span></a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>
	        <li class="active"><a href="#"><span class="glyphicon glyphicon-asterisk">  Scores</span></a></li>
	    </ul>  
	    <br/>        
            <!-- scores-->
            <c:if test="${not empty message}">                
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if> 			          	                        
			<h5>Select round: </h5>
            <form action="#" method="post" id="scoresForm" role="form" >
				<p>
					<form:select path="round.roundId" onchange="submit();">
					    <form:options items="${roundList}"/>
					</form:select>
				</p>              
                <input type="hidden" id="idScore" name="idScore">
                
                <c:choose>
                    <c:when test="${not empty scoreList}">
                        <table  class="table table-striped">
	                <caption>
	                    <span>Game: ${game.name} | Round: ${game.getRound(round.roundId).name} | </span>
	                    <span><fmt:formatDate value="${game.getRound(round.roundId).date}" pattern="dd.MM.yyyy HH:mm" /> | </span>
	                    <span>Bet: ${game.getRound(round.roundId).bet} EUR</span>
	                </caption>
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td><span class="nowrap">Player</span></td>
                                    <td>1</td>
                                    <td>2</td>
                                    <td>3</td>
                                    <td>4</td>
                                    <td>5</td>
                                    <td>6</td>
                                    <td>7</td>
                                    <td>8</td>
                                    <td>9</td>
                                    <td>Out</td>
                                    <td>10</td>
                                    <td>11</td>
                                    <td>12</td>
                                    <td>13</td>
                                    <td>14</td>
                                    <td>15</td>
                                    <td>16</td>
                                    <td>17</td>
                                    <td>18</td>
                                    <td>In</td>
                                    <td>Total</td>
                                    <td class="glyphicon glyphicon-eur"></td>
                                    <td></td>
                                    <td></td>
                                </tr>  
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td><small>${game.getRound(round.roundId).getPar(1)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(2)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(3)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(4)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(5)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(6)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(7)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(8)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(9)}</small></td>
                                    <td></td>
                                    <td><small>${game.getRound(round.roundId).getPar(10)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(11)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(12)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(13)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(14)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(15)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(16)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(17)}</small></td>
                                    <td><small>${game.getRound(round.roundId).getPar(18)}</small></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>                                                              
                            </thead>
                            <c:forEach var="score" items="${scoreList}" varStatus="loop">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idScore == score.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>
                                        <span>${loop.index +1}</span>
                                    </td>                                    
                                    <td><a href="/score/edit/${score.id}" class="nowrap">${fn:substring(score.player.firstName, 0, 1)}. ${score.player.lastName}</a></td>
                                    <td class="${score.isWinner(1) ? 'bg-danger text-danger' : ''}">${score.hole1}</td>
                                    <td class="${score.isWinner(2) ? 'bg-danger text-danger' : ''}">${score.hole2}</td>
                                    <td class="${score.isWinner(3) ? 'bg-danger text-danger' : ''}">${score.hole3}</td>
                                    <td class="${score.isWinner(4) ? 'bg-danger text-danger' : ''}">${score.hole4}</td>
                                    <td class="${score.isWinner(5) ? 'bg-danger text-danger' : ''}">${score.hole5}</td>
                                    <td class="${score.isWinner(6) ? 'bg-danger text-danger' : ''}">${score.hole6}</td>
                                    <td class="${score.isWinner(7) ? 'bg-danger text-danger' : ''}">${score.hole7}</td>
                                    <td class="${score.isWinner(8) ? 'bg-danger text-danger' : ''}">${score.hole8}</td>
                                    <td class="${score.isWinner(9) ? 'bg-danger text-danger' : ''}">${score.hole9}</td>
                                    <td>${score.countOut}</td>
                                    <td class="${score.isWinner(10) ? 'bg-danger text-danger' : ''}">${score.hole10}</td>
                                    <td class="${score.isWinner(11) ? 'bg-danger text-danger' : ''}">${score.hole11}</td>
                                    <td class="${score.isWinner(12) ? 'bg-danger text-danger' : ''}">${score.hole12}</td>
                                    <td class="${score.isWinner(13) ? 'bg-danger text-danger' : ''}">${score.hole13}</td>
                                    <td class="${score.isWinner(14) ? 'bg-danger text-danger' : ''}">${score.hole14}</td>
                                    <td class="${score.isWinner(15) ? 'bg-danger text-danger' : ''}">${score.hole15}</td>
                                    <td class="${score.isWinner(16) ? 'bg-danger text-danger' : ''}">${score.hole16}</td>
                                    <td class="${score.isWinner(17) ? 'bg-danger text-danger' : ''}">${score.hole17}</td>
                                    <td class="${score.isWinner(18) ? 'bg-danger text-danger' : ''}">${score.hole18}</td>
                                    <td>${score.countIn}</td>
                                    <td>${score.countTotal}</td>
                                    <td>${score.win}</td>
                                    <td><a href="/score/remove/${score.id}" id="remove"> 
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>                                                   
                                    </td>
                                </tr>
                            </c:forEach>               
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No scorecards found matching your search criteria
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
            <form action ="/score/add/${round.roundId}">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New Scorecard</button> 
            </form>
        </div>
    </body>
</html>