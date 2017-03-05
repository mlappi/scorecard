package fi.mlappi.golf.scorecard;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import fi.mlappi.golf.game.GameService;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.player.PlayerService;

public class ScorecardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	GameService gameService = new GameService(null, null, null);
	ScorecardService scoreService = new ScorecardService();
	//@Autowired
	PlayerService playerService;
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("searchAction");
        if (action!=null){
            switch (action) {           
            case "searchById":
                searchScoreById(req, resp);
                break;           
            case "searchByGame":
                searchByGameId(req, resp);
                break;
            }
        }else{
            List<Game> gameList = gameService.getAllGames();
            forwardListGames(req, resp, gameList);
        }
    }

    private void searchScoreById(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long idScore = Integer.valueOf(req.getParameter("idScore"));
        Scorecard scorecard = null;
        Player player = new Player();
        try {
            scorecard = scoreService.getScorecard(idScore);
            //player = playerService.getPlayer(scorecard.getPlayerId());

        } catch (Exception ex) {
            Logger.getLogger(ScorecardServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        req.setAttribute("player", player);
        req.setAttribute("scorecard", scorecard);
        req.setAttribute("action", "edit");
        String nextJSP = "/jsp/new-scorecard.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req, resp);
    }
    
    private void searchByGameId(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("gameName");
        List<Game> result = gameService.search(name);    
        forwardListScores(req, resp, result);
    }

    private void forwardListScores(HttpServletRequest req, HttpServletResponse resp, List scoreList)
            throws ServletException, IOException {
        String nextJSP = "/jsp/list-scores.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("scoreList", scoreList);
        dispatcher.forward(req, resp);
    }   

    private void forwardListGames(HttpServletRequest req, HttpServletResponse resp, List gameList)
            throws ServletException, IOException {
        String nextJSP = "/jsp/list-games.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("gameList", gameList);
        dispatcher.forward(req, resp);
    }   

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "add":
            	addScorecardAction(req, resp);
                break;
            case "edit":
            	editScoreAction(req, resp);
                break;            
            case "remove":
            	removeScoreById(req, resp);
                break;            
        }

    }

    private void addScorecardAction(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String date = req.getParameter("date");
        double bet = Double.parseDouble(req.getParameter("bet"));
        Game game = new Game();
        long idGame = gameService.save(game);
        List<Game> gameList = gameService.getAllGames();
        req.setAttribute("idGame", idGame);
        String message = "The new game has been successfully created.";
        req.setAttribute("message", message);
        forwardListGames(req, resp, gameList);
    }

    private void editScoreAction(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	List<Game> gameList = gameService.getAllGames();
    	forwardListGames(req, resp, gameList);
    }  

    private void removeScoreById(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	List<Game> gameList = gameService.getAllGames();
    	forwardListGames(req, resp, gameList);
    }

}
