import { createTheme } from "@mui/material/styles";

export const getTheme = (mode) =>
    createTheme({
        palette: {
            mode,
            primary: {
                main: mode === "light" ? "#1976d2" : "#90caf9",
            },
            background: {
                default: mode === "light" ? "#f4f6f8" : "#121212",
                paper: mode === "light" ? "#fff" : "#1e1e1e",
            },
        },
        shape: {
            borderRadius: 12,
        },
    });
