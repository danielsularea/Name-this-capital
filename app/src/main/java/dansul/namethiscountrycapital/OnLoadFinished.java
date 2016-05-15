package dansul.namethiscountrycapital;

import java.util.List;

import dansul.namethiscountrycapital.data.CountryCapital;


public interface OnLoadFinished {

    void finished(List<CountryCapital> countries);
}
