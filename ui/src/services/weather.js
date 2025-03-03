const API_ENDPOINT = window.env.WEATHER_ENDPOINT;

export const fetchWeather = async () => {
  const response = await fetch(`${API_ENDPOINT}/weather/rain-cell`);
  if (!response.ok) throw new Error(`Error fetching weather data: ${response.statusText}`);
  return response.json();
};
