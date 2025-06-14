import { useHistory } from "../hooks/useHistory";

import { useEffect, useState } from "react";
import { Box, Grid, Typography, Snackbar, Alert } from "@mui/material";
import LogViewer from "../components/Home/LogViewer";
import HistorySelector from "../components/History/HistorySelector";

function HistorySection() {
    const {
        historyList,
        fetchHistoryList,
        fetchHistoryDetails,
        historyDetails,
        loading,
        error,
    } = useHistory();

    const [selectedHistory, setSelectedHistory] = useState(null);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [alertMessage, setAlertMessage] = useState(null);

    useEffect(() => {
        fetchHistoryList();
    }, []);

    useEffect(() => {
        if (error) showSnackbar("error", error.message);
    }, [error]);

    const showSnackbar = (type, message) => {
        setAlertMessage({ type: type, text: message });
        setSnackbarOpen(true);
    }

    const closeSnackbar = () => {
        setSnackbarOpen(false);
        setAlertMessage(null);
    }

    useEffect(() => {
        if (selectedHistory) {
            fetchHistoryDetails(selectedHistory.id);
        }
    }, [selectedHistory]);

    return (
        <Box sx={{ width: '100%', maxWidth: { sm: '100%', md: '1700px' } }}>

            <HistorySelector historyList={historyList} onSelectHistory={setSelectedHistory} />

            {historyDetails && (
                <div>
                    <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
                        Execution Logs
                    </Typography>
                    <LogViewer logs={historyDetails.logs} />
                </div>
            )}

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

export default HistorySection;
