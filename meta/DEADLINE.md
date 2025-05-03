# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

> Please provide a friendly description of your app, including
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

GitHub Repo URL: `https://github.com/Monkey832m/cs1302-api-app`

Weather Music is a basic weather app where users can search for the current
weather conditions of a desired location, view said weather, and also get 5 
song recommendations from said weather conditions, courtesy of AccuWeather and
iTunes.

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### AccuWeather: Locations (Text Search)

```
http://dataservice.accuweather.com/locations/v1/search?apikey=k7lOavGLUcon5QNdHIImdpYwAOUCvlCn&q=Athens%2C%20GA&details=true
```

> Sends a call to AccuWeather's Location Text Search API in order to return
> a location key, which is needed in the call to Current Conditions to get
> the weather.

### AccuWeather: Current Conditions

```
http://dataservice.accuweather.com/currentconditions/v1/328217?apikey=k7lOavGLUcon5QNdHIImdpYwAOUCvlCn&details=true
```

> This gets the current weather conditions from AccuWeather. "328217" in the
> example is the location key from the Text Search API. This API also returns
> a variable called weatherText, which is passed to the iTunes API as the search
> term to find song recommendations.

### iTunes

```
https://itunes.apple.com/search?term=Sunny&media=music&limit=5
```

> iTunes API to get 5 recommended songs based on the current weather conditions.
> And by based on, I mean I'm searching the iTunes API with the word "Sunny" or
> "Mostly Cloudy" and getting 5 songs from that because I thought it was funny.
> To clarify: YES, it is supposed to work like that.

## Part 2: New

> What is something new and/or exciting that you learned from working on this project?

I was able to learn a lot more about JavaFX, JSON, and APIs. I also learned
a small Gson fix for AccuWeather's APIs returning variable names in Pascal case,
which caused 17 check1302 errors, but the Gson fix solved it.

## Part 3: Retrospect

> If you could start the project over from scratch, what do you think might do differently and why?

I would definitely start it a bit earlier so I would have more time to polish
the UI.
