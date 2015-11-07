import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Simulation de l'expérience menée dans la vidéo : https://www.youtube.com/watch?v=IPK6BXQ9HCQ.
 * 
 * @author Jonathan Guéhenneux
 */
public class PoneyForEver {

	static final int JONGLEUR_GAUCHE = 0;
	static final int JONGLEUR_DROITE = 1;
	static final int PONEY = 2;
	static final int HERO = 3;

	static final double DUREE_COUTEAU = 4.4;
	static final Random RANDOM = new Random();

	public static void main(String[] args) {

		PoneyForEver board;
		int couteaux;
		int total;

		Map<Integer, Integer> statistiques = new HashMap<>();
		int essais = 0;
		int nombreEssais = 100000;

		// LANCEMENT DES TESTS

		while (essais++ < nombreEssais) {

			// on réinitialise le board
			board = new PoneyForEver(5, 1_073_741_780, 1_073_741_780, 9, 5, 1_073_741_780, 1_073_741_780, 12);

			total = 0;

			// pour déclencher la réaction en chaîne un poney du haut attaque un poney du bas
			// conséquence : 2 couteaux partent vers le bas mais un poney du bas est déjà mort
			couteaux = 2;
			board.nombrePoneysBas--;
			total += couteaux;
			couteaux = board.lancerBas(couteaux);

			// on ressuscite le poney du bas, dorénavant, les poneys seront tous vivants avant un lancer de couteaux
			board.nombrePoneysBas++;

			// la résurrection du poney (tué par le poney du haut, pas par un couteau) déclenche 2 couteaux (on suppose,
			// pour simplifier, que les 2 jongleurs étaient vivants)
			couteaux += 2;

			// on boucle tant que des couteaux sont lancés
			while (couteaux > 0) {

				total += couteaux;
				couteaux = board.lancerHaut(couteaux);

				total += couteaux;
				couteaux = board.lancerBas(couteaux);
			}

			// le test est terminé, on ajoute le nombre de couteaux lancés aux statistiques
			if (statistiques.containsKey(total)) {
				statistiques.put(total, statistiques.get(total) + 1);
			} else {
				statistiques.put(total, 1);
			}
		}

		// on exploite les statistiques
		List<Integer> nombresCouteaux = new ArrayList<>(statistiques.keySet());
		Collections.sort(nombresCouteaux);
		int nombreFois;
		double duree;
		int cumulNombreFois = 0;
		int nombreCouteauxMedian = 0;

		for (int nombreCouteaux : nombresCouteaux) {

			nombreFois = statistiques.get(nombreCouteaux);

			if (cumulNombreFois <= nombreEssais / 2 && cumulNombreFois + nombreFois > nombreEssais / 2) {
				nombreCouteauxMedian = nombreCouteaux;
			}

			cumulNombreFois += nombreFois;

			duree = Math.round(nombreCouteaux * DUREE_COUTEAU);
			System.out.println(Math.round(duree) + "\t" + nombreFois);
		}

		duree = Math.round(nombreCouteauxMedian * DUREE_COUTEAU);
		System.out.println("durée médiane : " + duree + " secondes");
	}

	int nombrePoneysHaut;
	int santeJongleurGaucheHaut;
	int santeJongleurDroiteHaut;
	int santeHeroHaut;
	int nombrePoneysBas;
	int santeJongleurGaucheBas;
	int santeJongleurDroiteBas;
	int santeHeroBas;

	int[] cibles;
	int indexCible;

	PoneyForEver(int nombrePoneysHaut, int santeJongleurGaucheHaut, int santeJongleurDroiteHaut, int santeHeroHaut,
			int nombrePoneysBas, int santeJongleurGaucheBas, int santeJongleurDroiteBas, int santeHeroBas) {

		this.nombrePoneysHaut = nombrePoneysHaut;
		this.santeJongleurGaucheHaut = santeJongleurGaucheHaut;
		this.santeJongleurDroiteHaut = santeJongleurDroiteHaut;
		this.santeHeroHaut = santeHeroHaut;
		this.nombrePoneysBas = nombrePoneysBas;
		this.santeJongleurGaucheBas = santeJongleurGaucheBas;
		this.santeJongleurDroiteBas = santeJongleurDroiteBas;
		this.santeHeroBas = santeHeroBas;
	}

	/**
	 * Lance les couteaux vers le bas, met à jour les PV des jongleurs et du héro et comptabilise le poneys morts.
	 * 
	 * @param couteaux
	 *            le nombre de couteaux à lancer
	 * @return le nombre de couteaux à lancer vers le haut suite à la résurrection des poneys morts en bas
	 */
	int lancerBas(int couteaux) {

		List<Integer> cibles = new ArrayList<>();

		while (cibles.size() < nombrePoneysBas) {
			cibles.add(PONEY);
		}

		int jongleursBas = 0;

		if (santeJongleurGaucheBas > 0) {

			cibles.add(JONGLEUR_GAUCHE);
			jongleursBas++;
		}

		if (santeJongleurDroiteBas > 0) {

			cibles.add(JONGLEUR_DROITE);
			jongleursBas++;
		}

		if (santeHeroBas > 0) {
			cibles.add(HERO);
		}

		int couteau;
		int cible;

		int poneysMorts = 0;

		while (couteaux-- > 0) {

			couteau = RANDOM.nextInt(cibles.size());
			cible = cibles.get(couteau);

			switch (cible) {

			case PONEY:
				cibles.remove(couteau);
				poneysMorts++;
				break;

			case JONGLEUR_GAUCHE:
				santeJongleurGaucheBas--;
				if (santeJongleurGaucheBas == 0) {
					cibles.remove(couteau);
				}
				break;

			case JONGLEUR_DROITE:
				santeJongleurDroiteBas--;
				if (santeJongleurDroiteBas == 0) {
					cibles.remove(couteau);
				}
				break;

			case HERO:
				santeHeroBas--;
				if (santeHeroBas == 0) {
					cibles.remove(couteau);
				}
				break;
			}
		}

		return poneysMorts * jongleursBas;
	}

	/**
	 * Lance les couteaux vers le haut, met à jour les PV des jongleurs et du héro et comptabilise le poneys morts.
	 * 
	 * @param couteaux
	 *            le nombre de couteaux à lancer
	 * @return le nombre de couteaux à lancer vers le bas suite à la résurrection des poneys morts en haut
	 */
	int lancerHaut(int couteaux) {

		List<Integer> cibles = new ArrayList<>();

		while (cibles.size() < nombrePoneysHaut) {
			cibles.add(PONEY);
		}

		int jongleursHaut = 0;

		if (santeJongleurGaucheHaut > 0) {

			cibles.add(JONGLEUR_GAUCHE);
			jongleursHaut++;
		}

		if (santeJongleurDroiteHaut > 0) {

			cibles.add(JONGLEUR_DROITE);
			jongleursHaut++;
		}

		if (santeHeroHaut > 0) {
			cibles.add(HERO);
		}

		int couteau;
		int cible;

		int poneysMorts = 0;

		while (couteaux-- > 0) {

			couteau = RANDOM.nextInt(cibles.size());
			cible = cibles.get(couteau);

			switch (cible) {

			case PONEY:
				cibles.remove(couteau);
				poneysMorts++;
				break;

			case JONGLEUR_GAUCHE:
				santeJongleurGaucheHaut--;
				if (santeJongleurGaucheHaut == 0) {
					cibles.remove(couteau);
				}
				break;

			case JONGLEUR_DROITE:
				santeJongleurDroiteHaut--;
				if (santeJongleurDroiteHaut == 0) {
					cibles.remove(couteau);
				}
				break;

			case HERO:
				santeHeroHaut--;
				if (santeHeroHaut == 0) {
					cibles.remove(couteau);
				}
				break;
			}
		}

		return poneysMorts * jongleursHaut;
	}
}