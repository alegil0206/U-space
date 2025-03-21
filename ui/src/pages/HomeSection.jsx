import { Typography, Snackbar, Alert } from "@mui/material";
import { useState, useEffect } from "react";
import Grid from "@mui/material/Grid2";
import Box from "@mui/material/Box";
import DronesPositionsCard from "../components/Drone/DronesPositionsCard";
import DronesAuthorizationDataGrid from "../components/Authorization/DronesAuthorizationDataGrid";
import FullMap from "../components/Home/FullMap";
import LogViewer from "../components/Home/LogViewer";
import ExecutionControls from "../components/Home/ExecutionControls";

import { useGeoAuthorization } from "../hooks/useGeoAuthorization";
import { useDroneIdentification } from "../hooks/useDroneIdentification";
import { useGeoAwareness } from "../hooks/useGeoAwareness";
import { useWeather } from "../hooks/useWeather";
import GeoZoneActivationCard from "../components/GeoZone/GeoZoneActivationCard";

function HomeSection() {

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [alertMessage, setAlertMessage] = useState(null);

  const [dronesPositions, setDronesPositions] = useState([]);

  const { 
    geoZones,
    error: geoZonesError,
    fetchGeoZones,
    updateGeoZone
  } = useGeoAwareness();

  const {
    drones,
    error: dronesError,
    fetchDrones
  } = useDroneIdentification();

  const { 
    authorization,
    error: authorizationError,
    fetchAuthorizations,
    revokeAuthorization,
  } = useGeoAuthorization();

  const { 
    weather,
    error: weatherError, 
    fetchWeather} = useWeather();

  const showSnackbar = (type, message) => {
    setAlertMessage({ type: type, text: message });
    setSnackbarOpen(true);
  }

  const closeSnackbar = () => {
    setSnackbarOpen(false);
    setAlertMessage(null);
  }

  useEffect(() => {
    if (geoZonesError) showSnackbar("error", geoZonesError.message);
    if (authorizationError) showSnackbar("error", authorizationError.message);
    if (dronesError) showSnackbar("error", dronesError.message);
    if (weatherError) showSnackbar("error", weatherError.message);
  }, [geoZonesError, authorizationError, dronesError, weatherError]);

  useEffect(() => {
    const dronesWithPosition = drones.map((drone) => ({
      ...drone,
      position: { ...drone.source, height: 0 },
    }));
    setDronesPositions(dronesWithPosition);
  }, [drones]);

  useEffect(() => {
    const loadAllData = async () => {
      await Promise.all([
        fetchDrones(),
        fetchGeoZones(),
        fetchAuthorizations(),
        fetchWeather()
      ]);
    };
  
    loadAllData();
    const interval = setInterval(loadAllData, 10000);
    return () => clearInterval(interval);
  }, []);

  const handleRevoke = async (id) => {
    const revokedId = await revokeAuthorization(id);
    if (revokedId){
      showSnackbar("success", `Authorization ${id} revoked successfully.`);
      fetchAuthorizations();
    }
  };

  const handleToggleStatus = async (id) => {
    const zone = geoZones.find((z) => z.id === id);
    const updatedZone = { ...zone, status: zone.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' };
    const idUpdated = await updateGeoZone(updatedZone);
    if (idUpdated)  
      showSnackbar("success", `GeoZone ${idUpdated} status updated successfully.`);
    fetchGeoZones();
  };

  return (
    <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>
      {/* Cards */}
      <Grid container spacing={1} columns={12} sx={{ mb: (theme) => theme.spacing(2) }}
      >
        <Grid size={{ xs: 12, lg: 7 }}>
          <FullMap drones={ dronesPositions } geoZones={geoZones} weather={weather}/>
        </Grid>
        <Grid size={{ xs: 12, lg: 5 }}>
          <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
            Execution Controls
          </Typography>  
          <ExecutionControls />
          <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
            Execution Logs
          </Typography>
          <LogViewer />
        </Grid>
      </Grid>

      <Grid container spacing={1} columns={12} sx={{ mb: (theme) => theme.spacing(2) }}>
        <Grid size={{ xs: 12, md: 6}}>
      <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
            Drones Positions
          </Typography>
          <DronesPositionsCard data={dronesPositions} />
        </Grid>
          <Grid size={{ xs: 12, md: 6 }}>
          <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
            GeoZones Activation
          </Typography>
          <GeoZoneActivationCard data={geoZones} onToggleStatus={handleToggleStatus} />
        </Grid>
      </Grid>

      <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
        Authorization Requests
      </Typography>
      <DronesAuthorizationDataGrid drones={dronesPositions} geoZones={geoZones} authorization={authorization} onRevoke={handleRevoke} />

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={5000}
        onClose={closeSnackbar}
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      >
        <Alert severity={alertMessage?.type} onClose={closeSnackbar} variant="filled">
          {alertMessage?.text}
        </Alert>
      </Snackbar>    
  
    </Box>
  );
}

export default HomeSection;
