import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const API_CONFIG = "http://localhost:8081/api/configuration";
const API_COMPONENTS = "http://localhost:8081/api/components"; // 🔥 Для загрузки компонентов по ID

function money(v) {
    const n = Number(v || 0);
    return Number.isFinite(n) ? n.toLocaleString('ru-RU') + ' ₽' : "0 ₽";
}

function getItemName(item) {
    if (!item) return "Не выбрано";

    const { producer, model, name, ramType, size, watt, efficiencyRating } = item;

    if (name) return name;
    if (producer && model) return `${producer} ${model}`;
    if (ramType) return `${producer || ''} ${ramType}`;
    if (size && watt && efficiencyRating) return `${producer || ''} ${size} ${watt}W ${efficiencyRating}`;
    if (producer) return producer;

    return `ID ${item.id}`;
}

export default function SavedConfigurations() {
    const [configurations, setConfigurations] = useState([]);
    const [components, setComponents] = useState({}); // 🔥 Кэш всех компонентов
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [deletingId, setDeletingId] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [configToDelete, setConfigToDelete] = useState(null);

    // Универсальная функция для API запросов
    const apiRequest = async (url, options = {}) => {
        const config = {
            method: options.method || 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        const response = await fetch(url, config);

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login';
                throw new Error('Unauthorized');
            }
            throw new Error(`HTTP ${response.status}`);
        }

        return response.json();
    };

    // 🔥 Загрузка всех конфигураций + компонентов
    const fetchConfigurations = async () => {
        try {
            setLoading(true);
            setError("");

            // 1. Получаем список конфигураций
            const configsData = await apiRequest(`${API_CONFIG}/list`);
            const configs = Array.isArray(configsData) ? configsData : [];

            // 2. Собираем все уникальные ID компонентов
            const allComponentIds = new Set();
            configs.forEach(config => {
                ['cpuId', 'gpuId', 'motherboardId', 'ramId', 'psuId', 'pcCaseId'].forEach(field => {
                    if (config[field]) {
                        allComponentIds.add(config[field]);
                    }
                });
            });

            // 3. Загружаем компоненты по типам (эффективнее чем по каждому ID)
            const componentPromises = [
                fetch(`${API_COMPONENTS}/cpu`).then(r => r.json()),
                fetch(`${API_COMPONENTS}/gpu`).then(r => r.json()),
                fetch(`${API_COMPONENTS}/motherboard`).then(r => r.json()),
                fetch(`${API_COMPONENTS}/ram`).then(r => r.json()),
                fetch(`${API_COMPONENTS}/psu`).then(r => r.json()),
                fetch(`${API_COMPONENTS}/pc_case`).then(r => r.json()),
            ];

            const [cpuList, gpuList, mbList, ramList, psuList, caseList] = await Promise.all(componentPromises);

            // 4. Создаем кэш компонентов по ID
            const componentsCache = {};
            getItems(cpuList).forEach(item => componentsCache[item.id] = { ...item, type: 'cpu' });
            getItems(gpuList).forEach(item => componentsCache[item.id] = { ...item, type: 'gpu' });
            getItems(mbList).forEach(item => componentsCache[item.id] = { ...item, type: 'motherboard' });
            getItems(ramList).forEach(item => componentsCache[item.id] = { ...item, type: 'ram' });
            getItems(psuList).forEach(item => componentsCache[item.id] = { ...item, type: 'psu' });
            getItems(caseList).forEach(item => componentsCache[item.id] = { ...item, type: 'pcCase' });

            setComponents(componentsCache);
            setConfigurations(configs);
        } catch (err) {
            console.error('Ошибка загрузки:', err);
            setError("Не удалось загрузить конфигурации или компоненты");
        } finally {
            setLoading(false);
        }
    };

    function getItems(payload) {
        if (Array.isArray(payload)) return payload;
        if (payload?.items && Array.isArray(payload.items)) return payload.items;
        if (payload?.content && Array.isArray(payload.content)) return payload.content;
        if (payload?.data && Array.isArray(payload.data)) return payload.data;
        return [];
    }

    // 🔥 Функция получения компонента по ID
    const getComponentById = (componentId) => {
        return components[componentId] || null;
    };

    // 🔥 Получение всех компонентов конфигурации
    const getConfigComponents = (config) => {
        const componentList = [];

        if (config.cpuId) componentList.push(getComponentById(config.cpuId));
        if (config.gpuId) componentList.push(getComponentById(config.gpuId));
        if (config.motherboardId) componentList.push(getComponentById(config.motherboardId));
        if (config.ramId) componentList.push(getComponentById(config.ramId));
        if (config.psuId) componentList.push(getComponentById(config.psuId));
        if (config.pcCaseId) componentList.push(getComponentById(config.pcCaseId));

        return componentList.filter(Boolean); // убираем null
    };

    const openDeleteModal = (config) => {
        setConfigToDelete(config);
        setShowDeleteModal(true);
    };

    const handleDeleteConfirm = async () => {
        if (!configToDelete?.id) return;

        try {
            setDeletingId(configToDelete.id);
            await apiRequest(`${API_CONFIG}`, {
                method: 'DELETE',
                body: JSON.stringify({ id: configToDelete.id })
            });
            fetchConfigurations();
        } catch (err) {
            console.error('Ошибка удаления:', err);
            alert('Ошибка удаления конфигурации');
        } finally {
            setDeletingId(null);
            setShowDeleteModal(false);
            setConfigToDelete(null);
        }
    };

    const handleView = async (id) => {
        try {
            const config = await apiRequest(`${API_CONFIG}/id?id=${id}`);
            console.log('Конфигурация:', config);
            alert(`Загружена конфигурация ID: ${id}`);
        } catch (err) {
            console.error('Ошибка просмотра:', err);
        }
    };

    const handleEdit = async (id) => {
        try {
            const config = await apiRequest(`${API_CONFIG}/id?id=${id}`);
            sessionStorage.setItem('editConfig', JSON.stringify(config));
            window.location.href = `/configurator?edit=${id}`;
        } catch (err) {
            console.error('Ошибка загрузки для редактирования:', err);
            alert('Не удалось загрузить конфигурацию для редактирования');
        }
    };

    const handleToOrder = async (id) => {
        try {
            const response = await fetch(`${API_CONFIG}/toOrder`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ id }),
                redirect: 'follow'
            });

            if (response.redirected) {
                window.location.href = response.url;
                return;
            }
        } catch (err) {
            console.error('Ошибка отправки в заказ:', err);
            alert('Не удалось отправить в заказ');
        }
    };

    const handleSave = async (config) => {
        try {
            await apiRequest(`${API_CONFIG}`, {
                method: 'PUT',
                body: JSON.stringify(config)
            });
            alert('Конфигурация обновлена');
            fetchConfigurations(); // Обновляем список
        } catch (err) {
            console.error('Ошибка сохранения:', err);
            alert('Ошибка сохранения');
        }
    };

    useEffect(() => {
        fetchConfigurations();
    }, []);

    if (loading) {
        return (
            <div className="container py-5">
                <div className="d-flex justify-content-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Загрузка...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <>
            <div className="container py-4 py-lg-5">
                <div className="row mb-4 align-items-center">
                    <div className="col">
                        <h1 className="h2 mb-2">Мои конфигурации</h1>
                        <div className="text-muted">
                            {configurations.length} сохраненных сборок
                        </div>
                    </div>
                    <div className="col-auto">
                        <a href="/configurator" className="btn btn-success me-2" title="Создать новую сборку">
                            <i className="bi bi-plus-circle me-2"></i>
                            Новая сборка
                        </a>
                        <button className="btn btn-outline-primary" onClick={fetchConfigurations} disabled={loading}>
                            <i className="bi bi-arrow-clockwise me-2"></i>
                            Обновить
                        </button>
                    </div>
                </div>

                {error && (
                    <div className="alert alert-warning alert-dismissible fade show" role="alert">
                        <i className="bi bi-exclamation-triangle-fill me-2"></i>
                        {error}
                        <button type="button" className="btn-close" onClick={() => setError("")}></button>
                    </div>
                )}

                {configurations.length === 0 ? (
                    <div className="text-center py-5 my-5">
                        <div className="display-5 text-muted mb-4">
                            <i className="bi bi-archive-fill"></i>
                        </div>
                        <h2 className="h3 fw-normal text-muted mb-3">Конфигурации не найдены</h2>
                        <p className="lead text-muted mb-4">
                            Создайте свою первую сборку ПК в конфигураторе
                        </p>
                        <a href="/configurator" className="btn btn-success btn-lg">
                            <i className="bi bi-gear-fill me-2"></i>
                            Создать сборку
                        </a>
                    </div>
                ) : (
                    <div className="row g-4">
                        {configurations.map((config) => {
                            const configComponents = getConfigComponents(config);
                            const totalPrice = config.totalPrice ||
                                configComponents.reduce((sum, item) => sum + Number(item?.price || 0), 0);

                            return (
                                <div key={config.id} className="col-lg-6 col-xl-4">
                                    <div className="card h-100 shadow-sm hover-shadow-lg transition-all">
                                        <div className="card-body">
                                            <div className="d-flex justify-content-between align-items-start mb-3">
                                                <h5 className="card-title mb-0 fw-bold text-truncate" style={{ maxWidth: '220px' }}>
                                                    {config.name || `Конфигурация #${config.id}`}
                                                </h5>
                                                <div className="btn-group btn-group-sm" role="group">
                                                    <button className="btn btn-outline-primary" onClick={() => handleEdit(config.id)} title="Редактировать">
                                                        <i className="bi bi-pencil"></i>
                                                    </button>
                                                    <button className="btn btn-outline-secondary" onClick={() => handleView(config.id)} title="Посмотреть">
                                                        <i className="bi bi-eye"></i>
                                                    </button>
                                                    <button className="btn btn-outline-success" onClick={() => handleToOrder(config.id)} title="В заказ">
                                                        <i className="bi bi-cart"></i>
                                                    </button>
                                                    <button
                                                        className="btn btn-outline-danger"
                                                        onClick={() => openDeleteModal(config)}
                                                        disabled={deletingId === config.id}
                                                        title="Удалить"
                                                    >
                                                        {deletingId === config.id ?
                                                            <span className="spinner-border spinner-border-sm" /> :
                                                            <i className="bi bi-trash"></i>
                                                        }
                                                    </button>
                                                </div>
                                            </div>

                                            {/* 🔥 ВЫВОД ВСЕХ КОМПОНЕНТОВ */}
                                            <div className="mb-4" style={{ maxHeight: '240px', overflowY: 'auto' }}>
                                                {configComponents.length === 0 ? (
                                                    <div className="text-muted small text-center py-3">Компоненты не загружены</div>
                                                ) : (
                                                    configComponents.slice(0, 8).map((item, index) => (
                                                        <div key={item.id || index} className="d-flex justify-content-between mb-2 pb-1 border-bottom">
                                                            <span className="text-muted small text-capitalize">
                                                                {item.type || 'компонент'}
                                                            </span>
                                                            <div className="text-end">
                                                                <div className="small fw-medium text-truncate" style={{ maxWidth: '160px' }}>
                                                                    {getItemName(item)}
                                                                </div>
                                                                <div className="text-success fw-bold small">
                                                                    {money(item.price)}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    ))
                                                )}
                                                {configComponents.length > 8 && (
                                                    <small className="text-primary fw-medium d-block text-center">
                                                        +{configComponents.length - 8} компонентов
                                                    </small>
                                                )}
                                            </div>

                                            <hr className="my-3" />

                                            <div className="d-flex justify-content-between align-items-center">
                                                <div>
                                                    <div className="h5 mb-0 fw-bold text-success">
                                                        {money(totalPrice)}
                                                    </div>
                                                    <small className="text-muted">
                                                        {configComponents.length} компонентов
                                                    </small>
                                                </div>
                                                <button className="btn btn-outline-primary btn-sm px-3" onClick={() => handleSave(config)}>
                                                    <i className="bi bi-save me-1"></i>
                                                    Сохранить
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {/* Модальное окно удаления */}
            <div className="modal fade" tabIndex="-1" show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">
                                <i className="bi bi-exclamation-triangle text-warning me-2"></i>
                                Удалить конфигурацию?
                            </h5>
                            <button type="button" className="btn-close" onClick={() => setShowDeleteModal(false)}></button>
                        </div>
                        <div className="modal-body">
                            <p className="mb-0">
                                Вы уверены, что хотите удалить конфигурацию
                                <strong> "{configToDelete?.name || `Конфигурация #${configToDelete?.id}`}"</strong>?
                            </p>
                            <p className="text-muted small mt-2">
                                <i className="bi bi-info-circle me-1"></i>
                                Это действие нельзя отменить.
                            </p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" onClick={() => setShowDeleteModal(false)}>
                                Отмена
                            </button>
                            <button
                                type="button"
                                className="btn btn-danger"
                                onClick={handleDeleteConfirm}
                                disabled={deletingId === configToDelete?.id}
                            >
                                {deletingId === configToDelete?.id ? (
                                    <>
                                        <span className="spinner-border spinner-border-sm me-2" />
                                        Удаление...
                                    </>
                                ) : (
                                    <>
                                        <i className="bi bi-trash me-2"></i>
                                        Удалить
                                    </>
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}