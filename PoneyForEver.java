import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PoneyForEver {

	static final int JONGLEUR_GAUCHE = 0;
	static final int JONGLEUR_DROITE = 1;
	static final int PONEY = 2;
	static final int HERO = 3;

	static final Random RANDOM = new Random();

	public static void main(String[] args) {

		PoneyForEver board;
		int couteaux;
		int total;

		Map<Integer, Integer> statistiques = new HashMap<>();
		int essais = 0;
		int nombreEssais = 10000;

		// LANCEMENT DES TESTS

		while (essais++ < nombreEssais) {

			// on r�initialise le board
			board = new PoneyForEver(5, 1_073_741_780, 1_073_741_780, 9, 5, 1_073_741_780, 1_073_741_780, 12);

			total = 0;

			// pour d�clencher la r�action en cha�ne un poney du haut attaque un poney du bas
			// cons�quence : 2 couteaux partent vers le bas mais un poney du bas est d�j� mort
			couteaux = 2;
			board.nombrePoneysBas--;
			total += couteaux;
			couteaux = board.lancerBas(couteaux);

			// on r�cup�re le poney du bas, dor�navant, les poneys seront tous vivants avant un lancer de couteaux
			board.nombrePoneysBas++;

			// on boucle tant que des couteaux sont lanc�s
			while (couteaux > 0) {

				total += couteaux;
				couteaux = board.lancerHaut(couteaux);

				total += couteaux;
				couteaux = board.lancerBas(couteaux);
			}

			// le test est termin�, on ajoute le nombre de couteaux lanc�s aux statistiques
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
		long nombreCouteauxTotal = 0;
		double seuil = 3600;
		double nombreEssaisLongs = 0;

		for (int nombreCouteaux : nombresCouteaux) {

			duree = nombreCouteaux * 4.4;
			nombreCouteauxTotal += nombreCouteaux;
			nombreFois = statistiques.get(nombreCouteaux);

			if (duree >= seuil) {
				nombreEssaisLongs += nombreFois;
			}

			System.out.println(Math.round(duree) + "\t" + nombreFois);
		}

		double pourcentageEssaiLong = 100 * nombreEssaisLongs / nombreEssais;
		System.out.println(pourcentageEssaiLong + "% d'essais sup�rieurs � " + seuil + " secondes");

		duree = Math.round(nombreCouteauxTotal * 4.4 / 1000);
		System.out.println("dur�e moyenne : " + duree + " secondes");
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