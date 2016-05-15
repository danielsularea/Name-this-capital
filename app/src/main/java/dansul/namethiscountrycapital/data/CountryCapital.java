package dansul.namethiscountrycapital.data;

/**
 * Model class
 */
public class CountryCapital {

    private String country, capital;
    private String[] choices;

    public CountryCapital() {
        choices = new String[5];
    }

    public CountryCapital(String country, String capital, String[] choices) {
        this.country = country;
        this.capital = capital;
        this.choices = choices;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }
}
