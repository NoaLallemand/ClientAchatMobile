package com.nl.clientachatmobile.Models.Data;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Article> articles;

    public ShoppingCart(List<Article> articles) {
        this.articles = articles;
    }

    public List<Article> getArticles() { return articles; }

    public int addArticle(Article nouveauArt) {
        boolean found = false;
        int i = 0;

        //Vérifie si cet article qu'on veut ajouter n'est par hasard pas déjà présent dans le vecteur d'articles...
        while(i < articles.size() && !found)
        {
            Article artCourant = articles.get(i);
            if(artCourant.getId() == nouveauArt.getId()) {
                found = true;
            }
            else {
                i++;
            }
        }

        if(found)
        {
            //On a trouvé le meme article deja existant -> on incremente simplement la quantite.
            int qteActuelle = articles.get(i).getQuantite();
            int nouvelleQte = qteActuelle + nouveauArt.getQuantite();
            articles.get(i).setQuantite(nouvelleQte);
            return i;
        }
        else
        {
            articles.add(nouveauArt);
            return (articles.size() - 1);
        }
    }

    public void clearCart() {
        articles.clear();
    }
}
