import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button
} from '@mui/material';
import Grid from "@mui/material/Grid2";
import PropTypes from 'prop-types';
import DroneDetailsForm from './DroneDetailsForm';
import DroneDrawMap from './DroneDrawMap';

import { useMapSettings } from '../../../hooks/useMapSettings';

export default function DroneFormDialog({ onClose, onSave, initialData = null, open }) {

  const { initialViewState, mapBounds, maxPitch, sky } = useMapSettings();

  const getDefaultDrone = () => ({
    id: '',
    name: '',
    model: '',
    operation_category: 'SPECIFIC',
    owner: '',
    battery: 0,
    adaptive_capabilities: {
      collision_avoidance: true,
      geo_awareness: true,
      auto_authorization: true,
      battery_management: true,
    },
    source: { latitude: initialViewState.latitude - 0.01, longitude: initialViewState.longitude - 0.01 },
    destination: { latitude: initialViewState.latitude + 0.01, longitude: initialViewState.longitude + 0.01 },
  });

  const [drone, setDrone] = useState(getDefaultDrone());
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (open) {
      setDrone(initialData || getDefaultDrone());
      setErrors({});
    } else {
      setDrone(getDefaultDrone());
      setErrors({});
    }
  }, [open, initialData]);

  const handleChange = (field, value) => {
    setDrone((prev) => ({
      ...prev,
      [field]: typeof value === 'object' ? { ...prev[field], ...value } : value,
    }));
  };

  const validateFields = () => {
    const newErrors = {};

    if (!drone.name) newErrors.name = 'Name is required.';
    if (!drone.model) newErrors.model = 'Model is required.';
    if (!drone.owner) newErrors.owner = 'Owner is required.';
    if (drone.battery <= 0) newErrors.battery = 'Flight autonomy must be greater than 0.';
    const validateCoordinates = (coord, type) => {
      if (coord.latitude < mapBounds[0][1] || coord.latitude > mapBounds[1][1]) {
        newErrors[`${type}_latitude`] = `Latitude must be between ${mapBounds[0][1]} and ${mapBounds[1][1]}.`;
      }
      if (coord.longitude < mapBounds[0][0] || coord.longitude > mapBounds[1][0]) {
        newErrors[`${type}_longitude`] = `Longitude must be between ${mapBounds[0][0]} and ${mapBounds[1][0]}.`;
      }
    };

    validateCoordinates(drone.source, 'source');
    validateCoordinates(drone.destination, 'destination');

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = () => {
    if (validateFields()) {
      onSave({ ...drone });
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="lg" fullWidth>
      <DialogTitle>{initialData ? 'Edit Drone' : 'Add New Drone'}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} columns={12}>
          <Grid size={{ xs: 12, md: 6 }}>
            <DroneDetailsForm
              drone={drone}
              errors={errors}
              handleChange={handleChange}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6 }}>
            <DroneDrawMap
              drone={drone}
              handleChange={handleChange}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">
          Cancel
        </Button>
        <Button onClick={handleSave} color="primary">
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
}

DroneFormDialog.propTypes = {
  onClose: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired,
  initialData: PropTypes.object,
  open: PropTypes.bool.isRequired,
};
