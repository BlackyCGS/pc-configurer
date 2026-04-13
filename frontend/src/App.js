import { useState } from "react";
import { ThemeProvider, CssBaseline, IconButton, Box } from "@mui/material";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import { getTheme } from "./theme";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";

import LightModeIcon from "@mui/icons-material/LightMode";
import DarkModeIcon from "@mui/icons-material/DarkMode";
import CartPage from "./pages/CartPage";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

export default function App() {
    const [mode, setMode] = useState("light");

    const toggleTheme = () => {
        setMode((prev) => (prev === "light" ? "dark" : "light"));
    };

    return (
        <ThemeProvider theme={getTheme(mode)}>
            <CssBaseline />

            <BrowserRouter>
                <Box sx={{ position: "absolute", top: 20, right: 20 }}>
                    <IconButton onClick={toggleTheme} color="inherit">
                        {mode === "light" ? <DarkModeIcon /> : <LightModeIcon />}
                    </IconButton>
                    <IconButton
                        onClick={() => window.location.href = '/cart'}
                        color="inherit"
                        title="Корзина"
                    >
                        <ShoppingCartIcon />
                    </IconButton>
                </Box>

                <Routes>
                    <Route path="/cart" element={<CartPage />} />
                    <Route path="/configurator" element={<HomePage />} />
                    <Route path="/" element={<LoginPage />} />
                </Routes>
            </BrowserRouter>
        </ThemeProvider>
    );
}


