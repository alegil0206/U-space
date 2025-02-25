import Card from '@mui/material/Card';
import PropTypes from 'prop-types';
import Typography from '@mui/material/Typography';

export default function LocationCard({ data }) {
    return (
      <Card
        variant="outlined"
        sx={{ display: 'flex', flexDirection: 'column', gap: '8px', flexGrow: 1 }}
      >
        <Typography component="h2" variant="subtitle2" gutterBottom>{data.title}</Typography>
        <Typography variant="h4" component="p">{data.value}</Typography>
        <Typography component="h2" variant="subtitle2"  sx={{ color: 'text.secondary' }}>{data.coordinates}</Typography>
      </Card>
    );
  }
  
  LocationCard.propTypes = {
    data: PropTypes.object.isRequired,
};