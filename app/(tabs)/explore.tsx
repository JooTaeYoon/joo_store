import { FontAwesome6 } from '@expo/vector-icons';
import { router, useFocusEffect } from 'expo-router'; // useFocusEffect 추가
import React, { useCallback, useState } from 'react'; // useCallback 추가
import {
  ActivityIndicator,
  FlatList,
  RefreshControl,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

export default function CustomerListScreen() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchCustomers = async () => {
    try {
      // 안드로이드 에뮬레이터 루프백 IP 사용
      const response = await fetch('http://10.0.2.2:8080/api/store/get');
      const data = await response.json();
      setCustomers(data);
    } catch (error) {
      console.error('데이터 로드 실패:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  // ✅ 훅은 반드시 컴포넌트 내부에서 호출해야 합니다.
  // 이 탭을 누를 때마다(화면이 포커스될 때마다) 데이터를 새로 가져옵니다.
  useFocusEffect(
    useCallback(() => {
      fetchCustomers();
    }, []),
  );

  const onRefresh = () => {
    setRefreshing(true);
    fetchCustomers();
  };

  const renderItem = ({ item }: { item: any }) => (
    <TouchableOpacity
      style={styles.card}
      onPress={() =>
        router.push({
          pathname: `/customer/${item.id}`,
          params: { name: item.name },
        })
      }
    >
      <View style={styles.cardIcon}>
        <FontAwesome6 name="circle-user" size={30} color="#2e7d32" />
      </View>
      <View style={styles.cardContent}>
        <Text style={styles.nameText}>{item.name}</Text>
        <Text style={styles.phoneText}>{item.phoneNumber}</Text>
      </View>
      <FontAwesome6 name="chevron-right" size={16} color="#ccc" />
    </TouchableOpacity>
  );

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#2e7d32" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.headerTitle}>손님 목록</Text>
      <FlatList
        data={customers}
        keyExtractor={(item, index) => index.toString()}
        renderItem={renderItem}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        contentContainerStyle={{ paddingBottom: 20 }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    paddingHorizontal: 15,
    paddingTop: 60,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    color: '#333',
  },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    marginBottom: 12,
    elevation: 2, // Android 그림자
    shadowColor: '#000', // iOS 그림자
    shadowOpacity: 0.05,
    shadowRadius: 3,
  },
  cardIcon: { marginRight: 15 },
  cardContent: { flex: 1 },
  nameText: { fontSize: 17, fontWeight: '600', color: '#333' },
  phoneText: { fontSize: 14, color: '#777', marginTop: 2 },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
});
